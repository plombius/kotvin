package sk.plomba.kotvin.storage.sql.runtime

import sk.plomba.kotvin.platform.PlatformContext


/** Thrown by Kotvin DB calls when the platform driver reports an error. */
class KotvinSqlException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

interface KotvinDbHandler {
    @Throws(KotvinSqlException::class)
    fun connect(username: String, password: String, url: String)

    /** DDL/DML (no result set). Returns rows affected (if the platform can provide it). */
    @Throws(KotvinSqlException::class)
    fun execute(sql: String, params: List<Any?>): Int

    /** SELECT → map each row (as List<Any?>) to T. */
    @Throws(KotvinSqlException::class)
    fun <T> query(sql: String, params: List<Any?>, rowMapper: (List<Any?>) -> T): List<T>

    /** SELECT that must return exactly one row; throws if 0 or >1. */
    @Throws(KotvinSqlException::class)
    fun <T> querySingle(sql: String, params: List<Any?>, rowMapper: (List<Any?>) -> T): T

    @Throws(KotvinSqlException::class)
    fun checkTableExists(tableName: String): Boolean
}

expect fun getKotvinDbHandler(platformContext: PlatformContext): KotvinDbHandler