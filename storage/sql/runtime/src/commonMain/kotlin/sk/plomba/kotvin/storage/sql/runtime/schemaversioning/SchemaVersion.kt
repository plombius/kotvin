package sk.plomba.kotvin.storage.sql.runtime.schemaversioning


import sk.plomba.kotvin.storage.sql.runtime.KotvinEntity
import sk.plomba.kotvin.storage.sql.runtime.KotvinPrimaryKey
@KotvinEntity
data class SchemaVersion (
    @KotvinPrimaryKey
    val id: Int? = null,
    val name: String,
    val version: Int
)
