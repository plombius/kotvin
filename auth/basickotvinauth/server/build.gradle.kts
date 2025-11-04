plugins {
    alias(libs.plugins.kotlinJvm)
}

group = "sk.plomba.kotvin.auth.basickotvinauth.server"

dependencies {

    api(project(":networking:http:server"))
}

kotlin {
    jvmToolchain(21)
}