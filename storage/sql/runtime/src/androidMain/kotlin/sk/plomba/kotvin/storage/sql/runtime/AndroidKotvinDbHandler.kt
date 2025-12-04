package sk.plomba.kotvin.storage.sql.runtime


import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import sk.plomba.kotvin.platform.PlatformContext

actual fun getKotvinDbHandler(platformContext: PlatformContext): KotvinDbHandler {
    return AndroidKotvinDbHandler(platformContext.context)
}

class AndroidKotvinDbHandler(
    val kotvinAndroidContext: Context
): KotvinDbHandler {

    private class Helper(ctx: Context, name: String) : SQLiteOpenHelper(ctx, name, null, 1) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL("CREATE TABLE IF NOT EXISTS Achievement (\n" +
                    "  id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                    "  name TEXT NOT NULL,\n" +
                    "  location TEXT NOT NULL,\n" +
                    "  dateTime INTEGER NOT NULL,\n" +
                    "  duration INTEGER NOT NULL\n" +
                    ");")
        }
        override fun onUpgrade(db: SQLiteDatabase, oldV: Int, newV: Int) {}
    }
    private var adb: SQLiteDatabase? = null

    override fun connect(username: String, password: String, url: String) {
        try {
            adb = Helper(kotvinAndroidContext, if (url.isBlank()) "app.db" else url).writableDatabase
        } catch (t: Throwable) {
            throw KotvinSqlException("Android connect failed: ${t.message}", t)
        }
    }

    override fun execute(sql: String, params: List<Any?>): Int {
        val db = adb ?: throw KotvinSqlException("Not connected")
        try {
            db.compileStatement(sql).use { st ->
                params.forEachIndexed { i, v ->
                    val idx = i + 1
                    when (v) {
                        null -> st.bindNull(idx)
                        is Int -> st.bindLong(idx, v.toLong())
                        is Long -> st.bindLong(idx, v)
                        is Short -> st.bindLong(idx, v.toLong())
                        is Byte -> st.bindLong(idx, v.toLong())
                        is Boolean -> st.bindLong(idx, if (v) 1 else 0)
                        is Float -> st.bindDouble(idx, v.toDouble())
                        is Double -> st.bindDouble(idx, v)
                        is ByteArray -> st.bindBlob(idx, v)
                        else -> st.bindString(idx, v.toString())
                    }
                }
                return st.executeUpdateDelete()
            }
        } catch (t: Throwable) {
            throw KotvinSqlException("Android execute(bind) failed: ${t.message}", t)
        }
    }

    private fun Cursor.readRow(): List<Any?> {
        val row = ArrayList<Any?>(columnCount)
        for (i in 0 until columnCount) {
            row += when (getType(i)) {
                Cursor.FIELD_TYPE_NULL    -> null
                Cursor.FIELD_TYPE_INTEGER -> getLong(i)
                Cursor.FIELD_TYPE_FLOAT   -> getDouble(i)
                Cursor.FIELD_TYPE_STRING  -> getString(i)
                Cursor.FIELD_TYPE_BLOB    -> getBlob(i)
                else -> getString(i)
            }
        }
        return row
    }

    // Android SELECT binding: rawQuery binds String[] (still safe binding)
    private fun List<Any?>.toSelectionArgs(): Array<String?> =
        map { it?.toString() }.toTypedArray()

    override fun <T> query(sql: String, params: List<Any?>, rowMapper: (List<Any?>) -> T): List<T> {
        val db = adb ?: throw KotvinSqlException("Not connected")
        try {
            db.rawQuery(sql, params.toSelectionArgs()).use { c ->
                if (!c.moveToFirst()) return emptyList()
                val out = ArrayList<T>(c.count)
                do out += rowMapper(c.readRow()) while (c.moveToNext())
                return out
            }
        } catch (t: Throwable) {
            throw KotvinSqlException("Android query(bind) failed: ${t.message}", t)
        }
    }


    override fun <T> querySingle(sql: String, params: List<Any?>, rowMapper: (List<Any?>) -> T): T {
        val list = query(sql, params, rowMapper)
        if (list.isEmpty()) throw KotvinSqlException("Expected one row, got 0")
        if (list.size > 1) throw KotvinSqlException("Expected one row, got ${list.size}")
        return list.first()
    }

    override fun checkTableExists(tableName: String): Boolean {
        val db = adb ?: throw KotvinSqlException("Not connected")
        val sql = "SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name=?"
        return DatabaseUtils.longForQuery(db, sql, arrayOf(tableName)) > 0
    }
}