package sk.plomba.kotvin.auth.basickotvinauth.client.data


import sk.plomba.kotvin.auth.basickotvinauth.shared.ApiKeyResponse
import sk.plomba.kotvin.auth.basickotvinauth.shared.BasicLoginApi
import sk.plomba.kotvin.auth.basickotvinauth.shared.LoginRequest
import sk.plomba.kotvin.auth.basickotvinauth.shared.RecoverPasswordRequest
import sk.plomba.kotvin.auth.basickotvinauth.shared.RegisterRequest
import sk.plomba.kotvin.networking.http.shared.DefaultHttpErrorDto
import sk.plomba.kotvin.networking.http.shared.KotvinHttpCallResponse

class BasicAuthRepository(
    val basicAuthApi: BasicLoginApi
){
    suspend fun login(loginRequest: LoginRequest): KotvinHttpCallResponse<ApiKeyResponse, DefaultHttpErrorDto> {
        return basicAuthApi.login(loginRequest)
    }

    suspend fun register(registerRequest: RegisterRequest): KotvinHttpCallResponse<ApiKeyResponse, DefaultHttpErrorDto>{
        return basicAuthApi.register(registerRequest)
    }

    suspend fun recoverPassword(recoverPasswordRequest: RecoverPasswordRequest): KotvinHttpCallResponse<Unit, DefaultHttpErrorDto> {
        return basicAuthApi.recoverPassword(recoverPasswordRequest)
    }

}