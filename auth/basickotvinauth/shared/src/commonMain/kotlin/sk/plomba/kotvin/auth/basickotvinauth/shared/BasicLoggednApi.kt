package sk.plomba.kotvin.auth.basickotvinauth.shared

import sk.plomba.kotvin.networking.http.shared.Body
import sk.plomba.kotvin.networking.http.shared.DefaultHttpErrorDto
import sk.plomba.kotvin.networking.http.shared.Endpoint
import sk.plomba.kotvin.networking.http.shared.HttpApi
import sk.plomba.kotvin.networking.http.shared.KotvinHttpCallResponse

interface BasicLoggednApi : HttpApi{

    @Endpoint("POST", "/refresh")
    suspend fun refresh(
        @Body refreshRequest: RefreshRequest
    ): KotvinHttpCallResponse<ApiKeyResponse, DefaultHttpErrorDto>

    @Endpoint("POST", "/changeemail")
    suspend fun changeEmail(
        @Body changeEmailRequest: ChangeEmailRequest,
    ): KotvinHttpCallResponse<ApiKeyResponse, DefaultHttpErrorDto>
}