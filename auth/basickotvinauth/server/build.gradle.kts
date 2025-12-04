plugins {
    alias(libs.plugins.kotlinJvm)
}

group = "sk.plomba.kotvin.auth.basickotvinauth.server"

dependencies {

    api(project(":networking:http:server"))
    api(project(":storage:sql:runtime"))
}

kotlin {
    jvmToolchain(21)
}