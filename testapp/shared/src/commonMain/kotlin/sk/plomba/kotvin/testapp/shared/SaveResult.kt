package sk.plomba.kotvin.testapp.shared

import kotlinx.serialization.Serializable

@Serializable
data class SaveResultDto(
    val success: Boolean,
    val createdId: Int?
)