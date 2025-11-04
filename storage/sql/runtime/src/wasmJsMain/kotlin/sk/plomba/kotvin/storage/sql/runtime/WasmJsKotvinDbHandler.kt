package sk.plomba.kotvin.storage.sql.runtime

import kotlin.js.Promise

@JsModule("sql.js")
@JsNonModule
external fun initSqlJs(cfg: dynamic = definedExternally): Promise<dynamic>

private var jsDb: dynamic = null

// tiny helper to create a JS object literal in Kotlin/JS
private inline fun <reified T> jsObject(builder: T.() -> Unit): T {
    val o = js("({})")
    return o.unsafeCast<T>().apply(builder)
}

class WasmJsKotvinDbHandler : KotvinDbHandler() {
    override fun connect(username: String, password: String, url: String) {
        try {
            if (jsDb == null) {
                // Build the config object WITHOUT kotlin.js.json
                val cfg = jsObject<dynamic> {
                    // point to where sql-wasm.wasm is served in your app
                    this.locateFile = { _: String -> "/sql-wasm.wasm" }
                }
                initSqlJs(cfg).then { mod ->
                    jsDb = mod.Database() // in-memory
                }
            }
        } catch (t: Throwable) {
            throw KotvinSqlException("sql.js init failed: ${t.message}", t)
        }
    }

    override fun execute(sql: String): Int {
        ensureReady()
        try {
            jsDb.exec(sql)
            val ch = jsDb.exec("SELECT changes() AS c")
            return if (ch.isNotEmpty() && ch[0].values.length > 0) (ch[0].values[0][0] as Int) else 0
        } catch (t: Throwable) {
            throw KotvinSqlException("sql.js execute failed: ${t.message}", t)
        }
    }

    override fun <T> query(sql: String, rowMapper: (List<Any?>) -> T): List<T> {
        ensureReady()
        try {
            val res = jsDb.exec(sql) as Array<dynamic>
            if (res.isEmpty()) return emptyList()
            val values = res[0].values as Array<Array<dynamic>>
            val out = mutableListOf<T>()
            for (row in values) {
                val list = ArrayList<Any?>(row.size)
                for (i in row.indices) list += row[i]
                out += rowMapper(list)
            }
            return out
        } catch (t: Throwable) {
            throw KotvinSqlException("sql.js query failed: ${t.message}", t)
        }
    }

    override fun <T> querySingle(sql: String, rowMapper: (List<Any?>) -> T): T {
        val rows = query(sql, rowMapper)
        if (rows.isEmpty()) throw KotvinSqlException("Expected one row, got 0")
        if (rows.size > 1) throw KotvinSqlException("Expected one row, got ${rows.size}")
        return rows.first()
    }

    private fun ensureReady() {
        if (jsDb == null) throw KotvinSqlException("sql.js not initialized yet; call connect() first")
    }
}