package sk.plomba.kotvin.storage.sql.runtime

import sk.plomba.kotvin.platform.PlatformContext
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement

actual fun getKotvinDbHandler(platformContext: PlatformContext): KotvinDbHandler {
    return JvmKotvinDbHandler()
}

class JvmKotvinDbHandler: KotvinDbHandler {

    private var conn: Connection? = null

    override fun connect(username: String, password: String, url: String) {
        try {
            conn = if (username.isNotEmpty() || password.isNotEmpty())
                DriverManager.getConnection(url, username, password)
            else DriverManager.getConnection(url)
        } catch (t: Throwable) {
            throw KotvinSqlException("JDBC connect failed: ${t.message}", t)
        }
    }

    private fun PreparedStatement.bind(params: List<Any?>) {
        params.forEachIndexed { i, v ->
            val idx = i + 1
            when (v) {
                null -> this.setObject(idx, null)
                is Int -> this.setInt(idx, v)
                is Long -> this.setLong(idx, v)
                is Short -> this.setShort(idx, v)
                is Byte -> this.setByte(idx, v)
                is Boolean -> this.setBoolean(idx, v)
                is Float -> this.setFloat(idx, v)
                is Double -> this.setDouble(idx, v)
                is ByteArray -> this.setBytes(idx, v)
                else -> this.setString(idx, v.toString())
            }
        }
    }

    override fun execute(sql: String, params: List<Any?>): Int = try {
        conn?.prepareStatement(sql)?.use { st -> st.bind(params); st.executeUpdate() }
            ?: throw KotvinSqlException("Not connected")
    } catch (t: Throwable) {
        throw KotvinSqlException("JDBC execute(bind) failed: ${t.message}", t)
    }

    override fun <T> query(sql: String, params: List<Any?>, rowMapper: (List<Any?>) -> T): List<T> = try {
        conn?.prepareStatement(sql)?.use { st ->
            st.bind(params)
            st.executeQuery().use { rs ->
                val n = rs.metaData.columnCount
                buildList {
                    while (rs.next()) {
                        val row = ArrayList<Any?>(n)
                        for (i in 1..n) row += rs.getObject(i)
                        add(rowMapper(row))
                    }
                }
            }
        } ?: throw KotvinSqlException("Not connected")
    } catch (t: Throwable) {
        throw KotvinSqlException("JDBC query(bind) failed: ${t.message}", t)
    }


    override fun <T> querySingle(sql: String, params: List<Any?>, rowMapper: (List<Any?>) -> T): T {
        val list = query(sql, params, rowMapper)
        if (list.isEmpty()) throw KotvinSqlException("Expected one row, got 0")
        if (list.size > 1) throw KotvinSqlException("Expected one row, got ${list.size}")
        return list.first()
    }
}