package sk.plomba.kotvin.networking.http.shared


@Target(AnnotationTarget.FUNCTION)
annotation class Endpoint(val httpMethod: String, val path: String)

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Path(val name: String)

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Query(val name: String, val required: Boolean = false)

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Body
