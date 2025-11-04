package sk.plomba.kotvin.networking.http.shared

import kotlin.reflect.KFunction
import kotlin.reflect.KType

interface IHttpExecutor {
    suspend fun <RESPONSE, ERROR> call(
        fn: KFunction<*>,
        baseApiUrl: String,
        args: Array<Any?>,
        responseType: KType,
        errorType: KType,
        requestType: KType? = null
    ): KotvinHttpCallResponse<RESPONSE, ERROR>
}