package sk.plomba.kotvin.auth.basickotvinauth.shared

import sk.plomba.kotvin.networking.http.shared.Body
import sk.plomba.kotvin.networking.http.shared.DefaultHttpErrorDto
import sk.plomba.kotvin.networking.http.shared.Endpoint
import sk.plomba.kotvin.networking.http.shared.HttpApi
import sk.plomba.kotvin.networking.http.shared.KotvinHttpCallResponse
import sk.plomba.kotvin.networking.http.shared.Path
import sk.plomba.kotvin.networking.http.shared.Query

interface BasicLoginApi : HttpApi{

    @Endpoint("POST", "/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): KotvinHttpCallResponse<ApiKeyResponse, DefaultHttpErrorDto>

    @Endpoint("POST", "/register")
    suspend fun register(
        @Body registerRequest: RegisterRequest,
    ): KotvinHttpCallResponse<ApiKeyResponse, DefaultHttpErrorDto>

    @Endpoint("POST", "/recoverpass")
    suspend fun recoverPassword(
        @Body recoverPasswordRequest: RecoverPasswordRequest,
    ): KotvinHttpCallResponse<Unit, DefaultHttpErrorDto>

    @Endpoint("GET", "/verifyAccount/{userid}")
    suspend fun verifyAccount(
        @Path(name = "userId")
        @Query(name = "verification", required = true) verification: String,
    ): KotvinHttpCallResponse<Unit, DefaultHttpErrorDto>
}