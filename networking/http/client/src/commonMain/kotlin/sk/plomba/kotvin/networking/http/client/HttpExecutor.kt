package sk.plomba.kotvin.networking.http.client

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.appendAll
import sk.plomba.kotvin.networking.http.shared.Body
import sk.plomba.kotvin.networking.http.shared.Endpoint
import sk.plomba.kotvin.networking.http.shared.KotvinHttpCallResponse
import sk.plomba.kotvin.networking.http.shared.KotvinHttpResponseError
import sk.plomba.kotvin.networking.http.shared.Path
import sk.plomba.kotvin.networking.http.shared.Query
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.valueParameters


open class HttpExecutor(
    val baseUrl: String
) {

    open val httpClient = HttpClient(){
        defaultRequest {
            url("http://127.0.0.1:8080")
        }
        install(ContentNegotiation){
            json()
        }
    }

    open suspend fun <RESPONSE, ERROR> processResponse(
        kotvinHttpCallResponse: KotvinHttpCallResponse<RESPONSE, ERROR>
    ): KotvinHttpCallResponse<RESPONSE, ERROR>{
        return kotvinHttpCallResponse
    }


     suspend inline fun <reified REQUEST, reified RESPONSE, reified ERROR> call(
        fn: KFunction<*>,
        baseApiUrl: String,
        args: Array<Any?>
    ): KotvinHttpCallResponse<RESPONSE, ERROR> {
        val ep = fn.findAnnotation<Endpoint>() ?: error("Missing @Endpoint")
        val method = ep.httpMethod.uppercase()
        // Build path by replacing {seg} with @Path args
        var path = ep.path
        val queryParams = mutableMapOf<String, String>()
        var bodyObj: REQUEST? = null

        fn.valueParameters.forEachIndexed { i, p ->
            val arg = args[i]
            when {
                p.findAnnotation<Body>() != null -> bodyObj = arg as REQUEST
                p.findAnnotation<Path>() != null -> {
                    val name = p.findAnnotation<Path>()!!.name
                    path = path.replace("{$name}", (arg ?: "").toString())
                }
                p.findAnnotation<Query>() != null -> {
                    val meta = p.findAnnotation<Query>()!!
                    if (meta.required && arg == null) error("Missing required query '${meta.name}'")
                    if (arg != null) queryParams.put(meta.name, arg.toString())
                }
                else -> error("Parameter must have @Body or @Path or @Query")
            }
        }

        val url = buildString {
            append(baseUrl)
            append(baseApiUrl)
            append(path)
        }

        @Suppress("UNCHECKED_CAST")
        return executeHttp<REQUEST, RESPONSE, ERROR>(
            requestMethod = HttpMethod.parse(method),
            path = url,
            queryParams = queryParams,
            body = bodyObj
        )
    }

    suspend inline fun <reified REQUEST, reified RESPONSE, reified ERROR> executeHttp(
        requestMethod: HttpMethod,
        path: String,
        queryParams: Map<String, String>,
        body: REQUEST?
    ): KotvinHttpCallResponse<RESPONSE, ERROR>{
        try {
            val response = httpClient.request(baseUrl) {
                method = requestMethod
                contentType(ContentType.Application.Json)
                url {
                    path(path)
                    parameters.appendAll(queryParams)
                }
                setBody(body)
            }
            if(response.status.isSuccess()){
                return processResponse(
                    KotvinHttpCallResponse(
                        isSuccess = true,
                        responseCode = response.status.value,
                        data = response.body()
                    )
                )
            } else {
                return KotvinHttpCallResponse(
                    isSuccess = false,
                    responseCode = response.status.value,
                    error = KotvinHttpResponseError(hasApiError = true, apiError = response.body())
                )
            }
        } catch (e: Throwable){
            return KotvinHttpCallResponse(
                isSuccess = false,
                error = KotvinHttpResponseError(hasApiError = false, exception = e)
            )
        }
    }
}

class P (
    val httpExecutor: HttpExecutor
){
    suspend fun c(kFunction: KFunction<*>, url: String,  args: Array<Any?>): KotvinHttpCallResponse<*,*>{
        return httpExecutor.call<Any, Any, Any>(kFunction, url, args)
    }
}