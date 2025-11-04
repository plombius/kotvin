import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)          // <-- use the Android Library plugin
    alias(libs.plugins.kspPlugin)
    alias (libs.plugins.kotlinx.serialization)


}

kotlin {

    jvm()
    androidTarget()

    sourceSets {
        commonMain.dependencies {
            api(project(":storage:sql:kspProcessor"))
            api(project(":storage:sql:runtime"))
            api(libs.kotlinx.core)
            api(project(":networking:http:shared"))
            api(project(":networking:http:kspProcessor"))

        }


        androidMain.dependencies {
        }

        jvmMain.dependencies {

        }
    }
}

dependencies {
    add("kspCommonMainMetadata", project(":storage:sql:kspProcessor"))
    add("kspJvm", project(":storage:sql:kspProcessor"))
    add("kspAndroid", project(":storage:sql:kspProcessor"))


    add("kspCommonMainMetadata", project(":networking:http:kspProcessor"))
    add("kspJvm", project(":networking:http:kspProcessor"))
    add("kspAndroid", project(":networking:http:kspProcessor"))
}


android {
    namespace = "sk.plomba.kotvin.testapp.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()

    }
}
