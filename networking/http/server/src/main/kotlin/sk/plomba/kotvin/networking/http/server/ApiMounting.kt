package sk.plomba.kotvin.networking.http.server.sk.plomba.kotvin.networking.http.server

import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.http.*
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.jvmErasure
import kotlinx.serialization.json.Json
import sk.plomba.kotvin.networking.http.shared.Body
import sk.plomba.kotvin.networking.http.shared.Endpoint
import sk.plomba.kotvin.networking.http.shared.HttpApi
import sk.plomba.kotvin.networking.http.shared.KotvinHttpCallResponse
import sk.plomba.kotvin.networking.http.shared.Path
import sk.plomba.kotvin.networking.http.shared.Query
import kotlin.reflect.KClass
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.isSubclassOf

fun Route.mountAnnotatedApi(impl: Any) {

    val apiIfaces: List<KClass<*>> =
        impl::class
            .supertypes
            .mapNotNull { it.classifier as? KClass<*> }
            .filter { it.java.isInterface && it.isSubclassOf(HttpApi::class) }

    if(apiIfaces.size != 1){
        throw RuntimeException("Must implement exactly one HttpApi")
    }

    val iface = apiIfaces[0]



    for (fn in iface.declaredMemberFunctions) {
        val ep = fn.findAnnotation<Endpoint>() ?: continue
        val httpMethod = HttpMethod.parse(ep.httpMethod.uppercase())
        val template = ep.path

        route(template, httpMethod) {
            handle {
                val callArgs = arrayListOf<Any?>()
                // receiver first
                callArgs += impl

                // build arg list in declared order
                fn.parameters.drop(1).forEach { p ->
                    when {
                        p.findAnnotation<Body>() != null -> {
                            callArgs += call.receive(p.type.jvmErasure)
                        }
                        p.findAnnotation<Path>() != null -> {

                            val name = p.findAnnotation<Path>()!!.name
                            val raw = call.parameters[name] ?: throw UrlRequirementException("path param ${name} not found")
                            callArgs += coerce(raw, p.type.jvmErasure)
                        }
                        p.findAnnotation<Query>() != null -> {
                            val meta = p.findAnnotation<Query>()!!
                            val raw = call.request.queryParameters[meta.name]
                            if (meta.required && raw == null) throw UrlRequirementException("query param ${meta.name} not found")
                            callArgs += if (raw == null) null else coerce(raw, p.type.jvmErasure)
                        }
                        else -> throw UrlRequirementException("All parameters must be annotated")
                    }
                }

                val result = if (fn.isSuspend) fn.callSuspend(*callArgs.toTypedArray()) as KotvinHttpCallResponse<*, *>
                else fn.call(*callArgs.toTypedArray()) as KotvinHttpCallResponse<*, *>

                call.respond(
                    message = (if(result.isSuccess) result.data else result.error?.apiError) ?: Unit,
                    status = HttpStatusCode.fromValue(result.responseCode?: if(result.isSuccess) 200 else 500)
                )
            }
        }
    }
}

private fun coerce(raw: String, k: kotlin.reflect.KClass<*>): Any = when (k) {
    String::class -> raw
    Int::class -> raw.toInt()
    Long::class -> raw.toLong()
    Boolean::class -> raw.toBoolean()
    else -> raw
}