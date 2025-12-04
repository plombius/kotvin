package sk.plomba.kotvin.storage.sql.runtime.enginewrappers

interface SqlEngineWrapper {
    fun checkTableExists(): Boolean
}