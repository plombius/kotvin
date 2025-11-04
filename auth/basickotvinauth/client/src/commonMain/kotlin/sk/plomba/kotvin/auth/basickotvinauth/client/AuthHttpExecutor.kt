package sk.plomba.kotvin.auth.basickotvinauth.client

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.plugin
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import sk.plomba.kotvin.auth.basickotvinauth.shared.ApiKey
import sk.plomba.kotvin.auth.basickotvinauth.shared.BasicLoggednApi
import sk.plomba.kotvin.auth.basickotvinauth.shared.RefreshRequest
import sk.plomba.kotvin.networking.http.client.HttpExecutor
import sk.plomba.kotvin.networking.http.shared.AuthHttpError
import sk.plomba.kotvin.networking.http.shared.AuthHttpErrorType
import sk.plomba.kotvin.networking.http.shared.KotvinHttpCallResponse
import sk.plomba.kotvin.networking.http.shared.KotvinHttpResponseError


open class KotvinAuthHttpResponseError<ERROR>(
    hasApiError: Boolean,
    apiError: ERROR? = null,
    exception: Throwable? = null
): KotvinHttpResponseError<ERROR>(hasApiError, apiError, exception)

class KotvinAuthHttpCallResponse<DATA, ERROR>(
    data: DATA? = null,
    responseCode: Int? = null,
    isSuccess: Boolean,
    error: KotvinAuthHttpResponseError<ERROR>? = null
): KotvinHttpCallResponse<DATA, ERROR>(data, responseCode, isSuccess, error){

}

class AuthHttpExecutor(
    val loggednApi: BasicLoggednApi,

    val apiKey: MutableStateFlow<ApiKey?>,
    baseurl: String
): HttpExecutor(baseurl) {




    override val httpClient = HttpClient(){
        install(ContentNegotiation){
            json()
        }
    }



    init {

        httpClient.plugin(HttpSend).intercept { request ->
            val originalCall = execute(request)

            apiKey.value?.let {
                request.headers["AccessToken"] = it.accessToken

                val response: Any? = originalCall.body()
                if (response is AuthHttpError) {
                    if (response.authHttpErrorType == AuthHttpErrorType.EXPIRED_TOKEN) {
                        val refreshResponse =
                            loggednApi.refresh(RefreshRequest(it.refreshToken))
                        if (refreshResponse.isSuccess && refreshResponse.data != null) {
                            val apikeyResponse = refreshResponse.data
                            val apiKeyData = apikeyResponse?.apiKey
                            apiKey.value = apiKeyData
                            if(apiKeyData != null) {
                                request.headers["AccessToken"] = apiKeyData.accessToken
                                execute(request)
                            }
                        }
                    }
                }
                originalCall

            }?: originalCall
        }
    }


    override suspend fun <RESPONSE, ERROR> processResponse(
        kotvinHttpCallResponse: KotvinHttpCallResponse<RESPONSE, ERROR>
    ): KotvinAuthHttpCallResponse<RESPONSE, ERROR>{
        return KotvinAuthHttpCallResponse(
            kotvinHttpCallResponse.data,
            kotvinHttpCallResponse.responseCode,
            kotvinHttpCallResponse.isSuccess,
            kotvinHttpCallResponse.error?.let {
                KotvinAuthHttpResponseError(
                    it.hasApiError,
                    it.apiError,
                    it.exception
                )
            }
        )
    }
}