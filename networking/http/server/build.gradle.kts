plugins {
    alias(libs.plugins.kotlinJvm)
}

group = "sk.plomba.kotvin.networking.http.server"

dependencies {
    api(libs.ktor.server.core)
    api(libs.ktor.server.netty)
    api(libs.ktor.server.content.negotiation)
    api(libs.ktor.server.status.pages)
    api(libs.ktor.server.call.logging)
    implementation("ch.qos.logback:logback-classic:1.5.17")


    api(project(":networking:http:shared"))
}
tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}