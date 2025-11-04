package sk.plomba.kotvin.networking.http.shared


open class KotvinHttpResponseError<ERROR>(
    val hasApiError: Boolean,
    val apiError: ERROR? = null,
    val exception: Throwable? = null
)

open class KotvinHttpCallResponse<DATA, ERROR>(
    val data: DATA? = null,
    val responseCode: Int? = null,
    val isSuccess: Boolean,
    val error: KotvinHttpResponseError<ERROR>? = null
)