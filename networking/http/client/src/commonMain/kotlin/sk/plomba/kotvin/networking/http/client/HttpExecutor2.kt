package sk.plomba.kotvin.networking.http.client

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.ContentType.Application.Json
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.appendAll
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import sk.plomba.kotvin.networking.http.shared.Body
import sk.plomba.kotvin.networking.http.shared.Endpoint
import sk.plomba.kotvin.networking.http.shared.IHttpExecutor
import sk.plomba.kotvin.networking.http.shared.KotvinHttpCallResponse
import sk.plomba.kotvin.networking.http.shared.KotvinHttpResponseError
import sk.plomba.kotvin.networking.http.shared.Path
import sk.plomba.kotvin.networking.http.shared.Query
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.valueParameters

interface HttpExe{
    suspend fun <RESPONSE, ERROR> call(
        fn: KFunction<*>,
        baseApiUrl: String,
        args: Array<Any?>,
        responseType: KType,
        errorType: KType,
        requestType: KType? = null
    ): KotvinHttpCallResponse<RESPONSE, ERROR>
}

open class HttpExecutor2(
    val baseUrl: String
): IHttpExecutor {

    // You can still keep ContentNegotiation; we’ll serialize/deserialize manually
    open val httpClient = HttpClient() {
        defaultRequest {
            url("http://127.0.0.1:8080")
        }
        install(ContentNegotiation) {
            json()
        }
    }

    // A single Json instance to (de)serialize using runtime types
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
        explicitNulls = false
    }

    open suspend fun <RESPONSE, ERROR> processResponse(
        kotvinHttpCallResponse: KotvinHttpCallResponse<RESPONSE, ERROR>
    ): KotvinHttpCallResponse<RESPONSE, ERROR> = kotvinHttpCallResponse

    /**
     * NOTE: no reified generics here. We pass type info explicitly.
     *
     * @param responseType REQUIRED runtime type for success body
     * @param errorType REQUIRED runtime type for error body
     * @param requestType OPTIONAL runtime type for request body (if there is a @Body)
     */
    override suspend fun <RESPONSE, ERROR> call(
        fn: KFunction<*>,
        baseApiUrl: String,
        args: Array<Any?>,
        responseType: KType,
        errorType: KType,
        requestType: KType?
    ): KotvinHttpCallResponse<RESPONSE, ERROR> {
        val ep = fn.findAnnotation<Endpoint>() ?: error("Missing @Endpoint")
        val method = ep.httpMethod.uppercase()

        // Build path by replacing {seg} with @Path args
        var path = ep.path
        val queryParams = mutableMapOf<String, String>()
        var bodyObj: Any? = null

        fn.valueParameters.forEachIndexed { i, p ->
            val arg = args[i]
            when {
                p.findAnnotation<Body>() != null -> bodyObj = arg
                p.findAnnotation<Path>() != null -> {
                    val name = p.findAnnotation<Path>()!!.name
                    path = path.replace("{$name}", (arg ?: "").toString())
                }
                p.findAnnotation<Query>() != null -> {
                    val meta = p.findAnnotation<Query>()!!
                    if (meta.required && arg == null) error("Missing required query '${meta.name}'")
                    if (arg != null) queryParams[meta.name] = arg.toString()
                }
                else -> error("Parameter must have @Body or @Path or @Query")
            }
        }

        val url = buildString {
            append(baseUrl)
            append(baseApiUrl)
            append(path)
        }

        return executeHttp(
            requestMethod = HttpMethod.parse(method),
            absoluteUrl = url,
            queryParams = queryParams,
            body = bodyObj,
            responseType = responseType,
            errorType = errorType,
            requestType = requestType
        )
    }

    /**
     * Execute with explicit runtime types; no reified usage.
     */
    suspend fun <RESPONSE, ERROR> executeHttp(
        requestMethod: HttpMethod,
        absoluteUrl: String,
        queryParams: Map<String, String>,
        body: Any?,
        responseType: KType,
        errorType: KType,
        requestType: KType? = null
    ): KotvinHttpCallResponse<RESPONSE, ERROR> {
        try {
            val httpResponse = httpClient.request {
                method = requestMethod
                url(absoluteUrl) // absolute URL, no path() fiddling
                contentType(ContentType.Application.Json)

                // Query params
                queryParams.forEach { (k, v) -> url.parameters.append(k, v) }

                // Serialize body if present and we have its type
                if (body != null && requestType != null) {
                    val reqSer: KSerializer<Any?> =
                        json.serializersModule.serializer(requestType)
                    val payload = json.encodeToString(reqSer, body)
                    setBody(payload)
                }
            }

            val raw = httpResponse.bodyAsText()

            return if (httpResponse.status.isSuccess()) {
                val ser: KSerializer<Any?> =
                    json.serializersModule.serializer(responseType)
                @Suppress("UNCHECKED_CAST")
                val data = json.decodeFromString(ser, raw) as RESPONSE
                processResponse(
                    KotvinHttpCallResponse(
                        isSuccess = true,
                        responseCode = httpResponse.status.value,
                        data = data
                    )
                )
            } else {
                // try to decode API error; if it fails, we still return a structured error with hasApiError=false
                val err = runCatching {
                    val errSer: KSerializer<Any?> =
                        json.serializersModule.serializer(errorType)
                    @Suppress("UNCHECKED_CAST")
                    json.decodeFromString(errSer, raw) as ERROR
                }.getOrNull()

                KotvinHttpCallResponse(
                    isSuccess = false,
                    responseCode = httpResponse.status.value,
                    error = KotvinHttpResponseError(
                        hasApiError = err != null,
                        apiError = err,
                        exception = if (err == null) IllegalStateException("Failed to parse error body: $raw") else null
                    )
                )
            }
        } catch (t: Throwable) {
            return KotvinHttpCallResponse(
                isSuccess = false,
                error = KotvinHttpResponseError(hasApiError = false, exception = t)
            )
        }
    }
}


