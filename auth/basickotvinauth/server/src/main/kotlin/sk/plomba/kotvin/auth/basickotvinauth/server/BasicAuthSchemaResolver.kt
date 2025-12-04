package sk.plomba.kotvin.auth.basickotvinauth.server

import sk.plomba.kotvin.storage.sql.runtime.schemaversioning.KotvinSqlSchemaVersionResolver

class BasicAuthSchemaResolver : KotvinSqlSchemaVersionResolver {
    override fun getName(): String {
        return "basickotvinauth"
    }

    override fun getVersion(): Int {
        return 1
    }

    override fun onCreate() {
        TODO("Not yet implemented")
    }

    override fun onUpgrade(old: Int, new: Int) {
        TODO("Not yet implemented")
    }
}