plugins {
    alias(libs.plugins.kotlinJvm)

    id("com.github.johnrengelman.shadow") version "8.1.1" // fat jar
    application
}

group = "sk.plomba.kotvin.testapp.server"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(project(":networking:http:server"))
    implementation(project(":testapp:shared"))
}


tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}


application {
    // <-- set YOUR entrypoint (Kotlin file Main.kt => MainKt)
    mainClass.set("sk.plomba.kotvin.testapp.server.MainKt")
}

// Make the slim jar runnable too (optional)
tasks.jar {
    manifest { attributes["Main-Class"] = application.mainClass.get() }
}

// Configure the fat jar (Shadow uses runtimeClasspath automatically)
tasks.shadowJar {
    archiveClassifier.set("all")
    mergeServiceFiles()
    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
    manifest { attributes["Main-Class"] = application.mainClass.get() }
}

// Optional alias to keep your old command
tasks.register("shadowJarJvm") { dependsOn(tasks.shadowJar) }