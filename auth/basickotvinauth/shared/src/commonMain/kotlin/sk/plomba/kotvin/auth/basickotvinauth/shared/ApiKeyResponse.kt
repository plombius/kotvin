package sk.plomba.kotvin.auth.basickotvinauth.shared

import kotlinx.serialization.Serializable

@Serializable
data class ApiKeyResponse(
    val apiKey: ApiKey?
)