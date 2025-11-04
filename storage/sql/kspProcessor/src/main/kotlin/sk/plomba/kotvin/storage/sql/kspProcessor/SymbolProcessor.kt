package sk.plomba.kotvin.storage.sql.kspProcessor
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.visitor.KSTopDownVisitor
import java.io.OutputStreamWriter

import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*

class KotvinDaoProcessor(private val env: SymbolProcessorEnvironment) : SymbolProcessor {
    private val logger = env.logger
    private val codegen = env.codeGenerator

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(KotvinEntity::class.qualifiedName!!)
        symbols.forEach { symbol ->
            if (symbol is KSClassDeclaration) {
                runCatching { generateCode(symbol) }
                    .onFailure { t -> logger.error("DAO generation failed: ${t.message}", symbol) }
            }
        }
        return emptyList()
    }

    private fun generateCode(symbol: KSClassDeclaration) {
        require(symbol.classKind == ClassKind.CLASS) { "@KotvinEntity must be a class" }

        val packageName = symbol.packageName.asString()
        val className = symbol.simpleName.asString()
        val daoName = "${className}Dao"
        val outPkg = "$packageName.generated"

        val ctor = symbol.primaryConstructor ?: error("$className needs a primary constructor")

        // collect constructor params + allow @KotvinPrimaryKey on param or matching property
        val cols = ctor.parameters.map { p ->
            val name = p.name!!.asString()
            val t = p.type.resolve()
            val typeFq = t.declaration.qualifiedName!!.asString() // e.g., kotlin.Int
            val nullable = t.nullability != Nullability.NOT_NULL
            val isPkOnParam = p.annotations.any { it.shortName.asString() == KotvinPrimaryKey::class.simpleName }
            Col(name, typeFq, nullable, isPkOnParam)
        }.toMutableList()

        if (cols.none { it.isPk }) {
            val pkProps = symbol.getAllProperties()
                .filter { it.annotations.any { a -> a.shortName.asString() == KotvinPrimaryKey::class.simpleName } }
                .map { it.simpleName.asString() }
                .toSet()
            cols.replaceAll { c -> c.copy(isPk = c.name in pkProps || c.isPk) }
        }

        val pk = cols.firstOrNull { it.isPk } ?: error("No @KotvinPrimaryKey found in $className")
        val nonPk = cols.filterNot { it.isPk }

        val tableName = className // or read from @KotvinEntity(table) if you add it
        val selectCols = cols.joinToString(", ") { it.name }
        val insertCols = nonPk.joinToString(", ") { it.name }
        val insertQ = nonPk.joinToString(", ") { "?" }
        val updateSet = nonPk.joinToString(", ") { "${it.name} = ?" }

        val pkTypeStr = kotlinTypeString(pk.typeFq, pk.nullable)
        val entityFq = "$packageName.$className"

        val rowMapperBody = buildString {
            appendLine("      var i = 0")
            appendLine("      $className(")
            cols.forEachIndexed { idx, c ->
                val expr = toRowExpr(c.typeFq, !c.nullable)
                val comma = if (idx == cols.lastIndex) "" else ","
                appendLine("        ${c.name} = $expr$comma")
            }
            appendLine("      )")
        }

        val code = """
            |@file:Suppress("UNCHECKED_CAST")
            |package $outPkg
            |
            |import sk.plomba.kotvin.storage.sql.runtime.KotvinDbHandler
            |import $entityFq
            |
            |class $daoName(private val db: KotvinDbHandler) {
            |  private val rowMapper: (List<Any?>) -> $className = { row ->
            |$rowMapperBody
            |  }
            |
            |  // INSERT without PK (assumed autoincrement in DB)
            |  fun insert(entity: $className): Int {
            |    val sql = "INSERT INTO $tableName ($insertCols) VALUES ($insertQ)"
            |    val params = listOf(${nonPk.joinToString(", ") { "entity.${it.name}" }})
            |    return db.execute(sql, params)
            |  }
            |
            |  fun update(entity: $className): Int {
            |    val sql = "UPDATE $tableName SET $updateSet WHERE ${pk.name} = ?"
            |    val params = listOf(${nonPk.joinToString(", ") { "entity.${it.name}" }}, entity.${pk.name})
            |    return db.execute(sql, params)
            |  }
            |
            |  fun deleteById(id: $pkTypeStr): Int {
            |    val sql = "DELETE FROM $tableName WHERE ${pk.name} = ?"
            |    return db.execute(sql, listOf(id))
            |  }
            |
            |  fun findById(id: $pkTypeStr): $className {
            |    val sql = "SELECT $selectCols FROM $tableName WHERE ${pk.name} = ? LIMIT 1"
            |    return db.querySingle(sql, listOf(id), rowMapper)
            |  }
            |
            |  fun findAll(): List<$className> {
            |    val sql = "SELECT $selectCols FROM $tableName"
            |    return db.query(sql, emptyList(), rowMapper)
            |  }
            |}
        """.trimMargin()

        codegen.createNewFile(
            Dependencies(false, symbol.containingFile!!),
            outPkg, daoName, "kt"
        ).use { out -> OutputStreamWriter(out).use { it.write(code) } }
    }

    // -------- helpers (string-only) --------

    private data class Col(val name: String, val typeFq: String, val nullable: Boolean, val isPk: Boolean)

    private fun kotlinTypeString(fq: String, nullable: Boolean): String {
        val base = when (fq) {
            "kotlin.Int" -> "Int"
            "kotlin.Long" -> "Long"
            "kotlin.Short" -> "Short"
            "kotlin.Byte" -> "Byte"
            "kotlin.Double" -> "Double"
            "kotlin.Float" -> "Float"
            "kotlin.Boolean" -> "Boolean"
            "kotlin.String" -> "String"
            "kotlin.Char" -> "Char"
            "kotlin.ByteArray" -> "ByteArray"
            else -> fq.removePrefix("kotlin.")
        }
        return if (nullable) "$base?" else base
    }

    private fun toRowExpr(fq: String, nonNull: Boolean): String = when (fq) {
        "kotlin.Int"    -> "(row[i++] as Number?)${if (nonNull) "!!.toInt()" else "?.toInt()"}"
        "kotlin.Long"   -> "(row[i++] as Number?)${if (nonNull) "!!.toLong()" else "?.toLong()"}"
        "kotlin.Short"  -> "(row[i++] as Number?)${if (nonNull) "!!.toShort()" else "?.toShort()"}"
        "kotlin.Byte"   -> "(row[i++] as Number?)${if (nonNull) "!!.toByte()" else "?.toByte()"}"
        "kotlin.Double" -> "(row[i++] as Number?)${if (nonNull) "!!.toDouble()" else "?.toDouble()"}"
        "kotlin.Float"  -> "(row[i++] as Number?)${if (nonNull) "!!.toFloat()" else "?.toFloat()"}"
        "kotlin.Boolean"-> "(((row[i++] as Number?)${if (nonNull) "!!" else ""}).toInt() == 1)"
        "kotlin.String" -> "row[i++] as String${if (nonNull) "" else "?"}"
        "kotlin.Char"   -> "(row[i++] as String${if (nonNull) "" else "?"}).${if (nonNull) "first()" else "firstOrNull()"}"
        "kotlin.ByteArray" -> "row[i++] as ByteArray${if (nonNull) "" else "?"}"
        else -> "row[i++]${if (nonNull) "!!" else ""}"
    }
}
class TestProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return KotvinDaoProcessor(environment)
    }
}
