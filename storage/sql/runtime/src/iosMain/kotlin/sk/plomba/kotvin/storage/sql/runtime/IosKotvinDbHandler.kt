package sk.plomba.kotvin.storage.sql.runtime
/*
import kotlinx.cinterop.*
import sqlite3.*


class IosKotvinDbHandler : KotvinDbHandler {

    private var handle: CPointer<sqlite3>? = null

    override fun connect(username: String, password: String, url: String) {
        memScoped {
            val p = alloc<CPointerVar<sqlite3>>()
            val path = if (url.isBlank()) "app.sqlite" else url
            val rc = sqlite3_open(path, p.ptr)
            if (rc != SQLITE_OK) throw KotvinSqlException("sqlite open rc=$rc")
            handle = p.value
        }
    }

    override fun execute(sql: String): Int = memScoped {
        val db = handle ?: throw KotvinSqlException("Not connected")
        val rc = sqlite3_exec(db, sql, null, null, null)
        if (rc != SQLITE_OK) throw KotvinSqlException("sqlite exec rc=$rc")
        sqlite3_changes(db)
    }

    override fun <T> query(sql: String, rowMapper: (List<Any?>) -> T): List<T> = memScoped {
        val db = handle ?: throw KotvinSqlException("Not connected")
        val stmtVar = alloc<CPointerVar<sqlite3_stmt?>>()
        if (sqlite3_prepare_v2(db, sql, -1, stmtVar.ptr, null) != SQLITE_OK)
            throw KotvinSqlException("sqlite prepare failed")
        val st = stmtVar.value ?: throw KotvinSqlException("prepare returned null")
        try {
            val out = mutableListOf<T>()
            while (true) {
                when (val rc = sqlite3_step(st)) {
                    SQLITE_ROW -> {
                        val n = sqlite3_column_count(st)
                        val row = ArrayList<Any?>(n)
                        for (i in 0 until n) {
                            row += when (sqlite3_column_type(st, i)) {
                                SQLITE_INTEGER -> sqlite3_column_int64(st, i)
                                SQLITE_FLOAT   -> sqlite3_column_double(st, i)
                                SQLITE_TEXT    -> sqlite3_column_text(st, i)?.toKString()
                                SQLITE_BLOB    -> {
                                    val ptr = sqlite3_column_blob(st, i)
                                    val size = sqlite3_column_bytes(st, i)
                                    if (ptr != null && size > 0) ByteArray(size) { idx -> ptr[idx].toByte() } else null
                                }
                                SQLITE_NULL, else -> null
                            }
                        }
                        out += rowMapper(row)
                    }
                    SQLITE_DONE -> return@memScoped out
                    else -> throw KotvinSqlException("sqlite step rc=$rc")
                }
            }
        } finally {
            sqlite3_finalize(st)
        }
    }

    override fun <T> querySingle(sql: String, rowMapper: (List<Any?>) -> T): T {
        val rows = query(sql, rowMapper)
        if (rows.isEmpty()) throw KotvinSqlException("Expected one row, got 0")
        if (rows.size > 1) throw KotvinSqlException("Expected one row, got ${rows.size}")
        return rows.first()
    }
}*/