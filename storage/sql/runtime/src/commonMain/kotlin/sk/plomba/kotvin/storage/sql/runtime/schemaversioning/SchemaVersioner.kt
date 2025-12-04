package sk.plomba.kotvin.storage.sql.runtime.schemaversioning

import sk.plomba.kotvin.storage.sql.runtime.KotvinDbHandler
import sk.plomba.kotvin.storage.sql.runtime.KotvinSqlException

class SchemaVersioner(
    val kotvinDbHandler: KotvinDbHandler
){
    val schemaVersionDao: SchemaVersionDao = SchemaVersionDao(kotvinDbHandler)

    fun resolveSchemaVersions(
        schemas: List<KotvinSqlSchemaVersionResolver>
    ){

        if(!kotvinDbHandler.checkTableExists("SchemaVersion")){
            kotvinDbHandler.execute(
                "CREATE TABLE IF NOT EXISTS SchemaVersion (\n" +
                        "  id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                        "  name TEXT NOT NULL,\n" +
                        "  version INTEGER NOT NULL\n" +
                        ");", listOf())
        }

        val schemasVersions = schemaVersionDao.findAll()

        schemas.forEach { dbSchema ->
            val current = schemasVersions.find { it.name == dbSchema.getName() }
            if(current != null) {
                for(i in current.version .. dbSchema.getVersion() - 1){
                    dbSchema.onUpgrade(i, i + 1)
                }
            } else {
                dbSchema.onCreate()
            }
        }
    }
}

