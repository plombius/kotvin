package sk.plomba.kotvin.networking.http.kspProcessor

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import java.io.OutputStreamWriter


class HttpApiProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return HttpApiProcessor(environment)
    }
}

class HttpApiProcessor(
    private val env: SymbolProcessorEnvironment
) : SymbolProcessor {
    private val mode = env.options["httpapi.mode"] ?: "impl" // "impl" or "index"
    private val packages = env.options["packages"] ?: "impl" // "impl" or "index"

    private val logger = env.logger
    private val codegen = env.codeGenerator

    //private val PKG = "sk.plomba.kotvin.testapp.shared"
    private val HTTP_API_FQCN = "sk.plomba.kotvin.networking.http.shared.HttpApi"
    private val ENDPOINT_FQCN = "sk.plomba.kotvin.networking.http.shared.Endpoint"
    private val implSuffix = "Impl"
    private val httpExecutorFq = "sk.plomba.kotvin.networking.http.shared.IHttpExecutor"

    // de-dupe across rounds
    private val generated = mutableSetOf<String>()

    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {


        val deferred = mutableListOf<KSAnnotated>()

        try {

            val endpointFns = resolver
                .getSymbolsWithAnnotation(ENDPOINT_FQCN)
                .filterIsInstance<KSFunctionDeclaration>()
                .filter { it.containingFile != null }
                .filter { (it.parentDeclaration as? KSClassDeclaration)?.classKind == ClassKind.INTERFACE }
                .toList()

            val (validFns, invalidFns) = endpointFns.partition { it.validate() }
            deferred += invalidFns

            val byIface: Map<KSClassDeclaration, List<KSFunctionDeclaration>> =
                validFns.groupBy { it.parentDeclaration as KSClassDeclaration }
                    // optional: stable order inside each group
                    .mapValues { (_, fns) -> fns.sortedBy { it.simpleName.asString() } }
            byIface.forEach { (iface, fns) ->
                if (!iface.validate()) { deferred += iface; return@forEach }
                try {
                    generateImpl(resolver, iface, fns)
                } catch (t: Throwable) {
                    logger.error(
                        "Generation failed for ${iface.qualifiedName?.asString()}: ${t.message}\n${t.stackTraceToString()}",
                        iface
                    )
                }
            }
        } catch (t: Throwable) {
            logger.error("Top-level processing failed: ${t.message}\n${t.stackTraceToString()}")
        }

        return deferred
    }


    @OptIn(KspExperimental::class)
    fun findHttpApisInPackage(
        resolver: Resolver,
    ): Map<KSClassDeclaration, List<KSFunctionDeclaration>> {

        // 1) Grab all class declarations from the package
        val classes = resolver.getDeclarationsFromPackage(packages)
            .filterIsInstance<KSClassDeclaration>()
            .toList()

        // 2) Keep only interfaces that (directly or indirectly) extend HttpApi
        val httpApiIfaces = classes.filter { c ->
            c.classKind == ClassKind.INTERFACE &&
                    c.getAllSuperTypes()                // checks whole hierarchy
                        .any { st -> st.declaration.qualifiedName?.asString() == HTTP_API_FQCN }
        }

        // 3) For each such interface, keep only functions annotated with @Endpoint
        val result = buildMap {
            for (iface in httpApiIfaces) {
                val fns = iface.getDeclaredFunctions()
                    .filter { fn ->
                        fn.annotations.any { a ->
                            a.annotationType.resolve().declaration.qualifiedName?.asString() == ENDPOINT_FQCN
                        }
                    }
                    .toList()

                if (fns.isNotEmpty()) put(iface, fns)
            }
        }

        return result
    }

    private fun generateImpl(
        resolver: Resolver,
        iface: KSClassDeclaration,
        fns: List<KSFunctionDeclaration>
    ) {
        val pkg = iface.packageName.asString() + ".generated"
        val ifaceFq = iface.qualifiedName?.asString() ?: return
        val implName = iface.simpleName.asString() + implSuffix
        val fqImpl = "$pkg.$implName"

        if (!generated.add(fqImpl)) return

        // If functions come from classpath, containingFile is null -> use aggregating deps
        val sourceFiles = fns.mapNotNull { it.containingFile }
        val deps = if (sourceFiles.isEmpty())
            Dependencies(aggregating = true)
        else
            Dependencies(aggregating = false, *sourceFiles.toTypedArray())

        val file = try {
            codegen.createNewFile(deps, pkg, implName)
        } catch (_: kotlin.io.FileAlreadyExistsException) {
            return
        }

        OutputStreamWriter(file, Charsets.UTF_8).use { out ->
            out.appendLine("package $pkg")
            out.appendLine()
            out.appendLine("import kotlin.reflect.typeOf")
            out.appendLine("class $implName(")
            out.appendLine("    private val executor: $httpExecutorFq,")
            out.appendLine("    private val baseApiUrl: kotlin.String = \"\"")
            out.appendLine(") : $ifaceFq {")
            out.appendLine()

            fns.forEach { fn ->
                try {
                    out.appendLine(renderFunctionImpl(ifaceFq, fn))
                } catch (t: Throwable) {
                    logger.error("Failed to render ${fn.simpleName.asString()} in $ifaceFq: ${t.message}", fn)
                }
            }

            out.appendLine("}")
        }
    }

    private fun renderFunctionImpl(ifaceFq: String, fn: KSFunctionDeclaration): String {
        val sb = StringBuilder()
        val name = fn.simpleName.asString()

        // Params (defensive: synthesize names if missing)
        val paramsSig = fn.parameters.mapIndexed { idx, p ->
            val n = (p.name?.asString()).orEmpty().ifBlank { "p$idx" }
            val t = p.type.safeRender()
            val prefix = if (p.isVararg) "vararg " else ""
            "$prefix$n: $t"
        }.joinToString(", ")

        var argsPass = fn.parameters.mapIndexed { idx, p ->
            (p.name?.asString()).orEmpty().ifBlank { "p$idx" }
        }.joinToString(", ")

        argsPass = "arrayOf(${argsPass})"
        // REQUEST = @Body param type or Unit
        val bodyParam = fn.parameters.firstOrNull { p -> p.annotations.any { it.shortName.asString() == "Body" } }
        val requestType = bodyParam?.type?.safeRender() ?: "kotlin.Unit"

        // RESPONSE, ERROR from KotvinHttpCallResponse<RESPONSE, ERROR>
        val returnTypeSrc = fn.returnType?.safeRender() ?: "kotlin.Any?"
        val retResolved = fn.returnType?.resolve()
        val responseType = retResolved?.arguments?.getOrNull(0)?.type?.safeRender() ?: "kotlin.Any?"
        val errorType = retResolved?.arguments?.getOrNull(1)?.type?.safeRender() ?: "kotlin.Any?"

        sb.appendLine("    override suspend fun $name($paramsSig): $returnTypeSrc =")
        sb.append("        executor.call<$responseType, $errorType>($ifaceFq::$name, baseApiUrl")
        if (argsPass.isNotBlank()) sb.append(", ").append(argsPass)
        sb.append(", responseType = typeOf<${responseType}>()")
        sb.append(", errorType = typeOf<${errorType}>()")
        sb.appendLine(")")
        return sb.toString()
    }
}

/** Fully-qualified rendering with variance + nullability, null-safe fallbacks. */
private fun KSTypeReference.safeRender(): String = try {
    this.resolve().safeRender()
} catch (_: Throwable) { "kotlin.Any?" }

private fun KSType.safeRender(): String {
    val base = this.declaration.qualifiedName?.asString()
        ?: this.declaration.simpleName.asString()
    val args = if (this.arguments.isNotEmpty()) {
        this.arguments.joinToString(", ", prefix = "<", postfix = ">") { a ->
            val t = a.type?.safeRender() ?: "*"
            when (a.variance) {
                Variance.COVARIANT -> "out $t"
                Variance.CONTRAVARIANT -> "in $t"
                Variance.INVARIANT -> t
                Variance.STAR -> "*"
            }
        }
    } else ""
    val nullable = if (this.nullability == Nullability.NULLABLE) "?" else ""
    return base + args + nullable
}