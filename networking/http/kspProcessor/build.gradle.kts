plugins {
    kotlin("jvm")
    alias(libs.plugins.kspPlugin)

}

group = "sk.plomba.kotvin.networking.http.kspProcessor"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation(libs.ksp.processing.api)
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}