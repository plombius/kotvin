package sk.plomba.kotvin.networking.http.shared

import kotlinx.serialization.Serializable

enum class AuthHttpErrorType{
    INVALID_TOKEN, EXPIRED_TOKEN
}

interface AuthHttpError{
    val authHttpErrorType: AuthHttpErrorType?
}

@Serializable
data class DefaultHttpErrorDto (val message: String, val timestamp: Long, override val authHttpErrorType: AuthHttpErrorType? = null,
): AuthHttpError