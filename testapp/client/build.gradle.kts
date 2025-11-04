
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    id("com.github.johnrengelman.shadow") version "8.1.1" // fat jar

}

val mainClassJvm = "sk.plomba.kotvin.testapp.client.MainKt"

kotlin {

    jvm(){
        tasks.named<Jar>(artifactsTaskName).configure {
            manifest {
                attributes["Main-Class"] = "sk.plomba.kotvin.testapp.client.MainKt"
            }

        }

    }
    androidTarget()

    sourceSets {
        commonMain.dependencies {

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)

            implementation(project(":networking:http:client"))
            implementation(project(":auth:basickotvinauth:client"))
            api(project(":testapp:shared"))
            implementation(project(":platform"))
            implementation(project(":compose"))
            implementation(project(":forms"))
            implementation(kotlin("reflect"))

        }


        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)


        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}

android {
    namespace = "sk.plomba.kotvin.testapp.client"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}

compose.desktop {
    application {
        mainClass = "sk.plomba.kotvin.testapp.client.MainKt"
    }
}



// Make the slim jvmJar runnable (helps for dev)
tasks.named<Jar>("jvmJar") {
    manifest { attributes["Main-Class"] = mainClassJvm }
}

val jvmJarTask = tasks.named<Jar>("jvmJar")

// Use FQCN so we don't depend on an import resolving
tasks.register<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("jvmShadowJar") {
    group = "build"
    archiveBaseName.set(project.name)
    archiveClassifier.set("all")

    dependsOn(jvmJarTask)

    // include compiled JVM classes/resources
    from(zipTree(jvmJarTask.get().archiveFile))

    // pull all JVM runtime deps
    configurations = listOf(project.configurations.getByName("jvmRuntimeClasspath"))

    // merge ServiceLoader entries; avoid signature clashes
    mergeServiceFiles()
    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")

    manifest { attributes["Main-Class"] = mainClassJvm }
}

// convenience alias
tasks.register("shadowJarJvm") { dependsOn("jvmShadowJar") }