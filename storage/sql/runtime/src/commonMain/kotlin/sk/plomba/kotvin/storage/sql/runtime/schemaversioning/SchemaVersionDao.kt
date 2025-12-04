package sk.plomba.kotvin.storage.sql.runtime.schemaversioning

import sk.plomba.kotvin.storage.sql.runtime.KotvinDbHandler


class SchemaVersionDao(private val db: KotvinDbHandler) {
    private val rowMapper: (List<Any?>) -> SchemaVersion = { row ->
        var i = 0
        SchemaVersion(
            id = (row[i++] as Number?)?.toInt(),
            name = row[i++] as String,
            version = (row[i++] as Number?)!!.toInt()
        )
    }

    // INSERT without PK (assumed autoincrement in DB)
    fun insert(entity: SchemaVersion): Int {
        val sql = "INSERT INTO SchemaVersion (name, version) VALUES (?, ?)"
        val params = listOf(entity.name, entity.version)
        return db.execute(sql, params)
    }

    fun update(entity: SchemaVersion): Int {
        val sql = "UPDATE SchemaVersion SET name = ?, version = ? WHERE id = ?"
        val params = listOf(entity.name, entity.version, entity.id)
        return db.execute(sql, params)
    }

    fun deleteById(id: Int?): Int {
        val sql = "DELETE FROM SchemaVersion WHERE id = ?"
        return db.execute(sql, listOf(id))
    }

    fun findById(id: Int?): SchemaVersion {
        val sql = "SELECT id, name, version FROM SchemaVersion WHERE id = ? LIMIT 1"
        return db.querySingle(sql, listOf(id), rowMapper)
    }

    fun findAll(): List<SchemaVersion> {
        val sql = "SELECT id, name, version FROM SchemaVersion"
        return db.query(sql, emptyList(), rowMapper)
    }
}