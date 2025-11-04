package sk.plomba.kotvin.auth.basickotvinauth.shared

data class RegisterRequest (
    val email: String? = null,
    val username: String? = null,
    val password: String
){
}