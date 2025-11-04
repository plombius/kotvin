package sk.plomba.kotvin.testapp.shared

import kotlinx.serialization.Serializable
import sk.plomba.kotvin.storage.sql.kspProcessor.KotvinEntity
import sk.plomba.kotvin.storage.sql.kspProcessor.KotvinPrimaryKey

@Serializable
@KotvinEntity
data class Achievement (
    @KotvinPrimaryKey
    val id: Int? = null,
    val name: String,
    val location: String,
    val dateTime: Long,
    val duration: Long
)


