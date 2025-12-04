package sk.plomba.kotvin.storage.sql.runtime.schemaversioning

interface KotvinSqlSchemaVersionResolver{
    fun getName(): String
    fun getVersion(): Int
    fun onCreate()
    fun onUpgrade(old: Int, new: Int)
}