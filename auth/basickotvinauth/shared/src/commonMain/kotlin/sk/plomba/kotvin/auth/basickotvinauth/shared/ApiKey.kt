package sk.plomba.kotvin.auth.basickotvinauth.shared

data class ApiKey(
    val accessToken: String,
    val refreshToken: String
)
