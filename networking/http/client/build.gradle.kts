import com.android.build.api.dsl.androidLibrary
import org.gradle.kotlin.dsl.api

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)          // <-- use the Android Library plugin

    //alias(libs.plugins.android.kotlin.multiplatform.library)
}

kotlin {
    jvm()
    androidTarget()



    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktor.client.core)
            api(libs.ktor.client.content.negotiation)

            api(project(":networking:http:shared"))
            implementation(kotlin("reflect"))


        }
        jvmMain.dependencies {
            implementation(libs.ktor.client.java)

        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
    }
}

android {                                         // <-- top-level, not inside kotlin
    namespace = "sk.plomba.kotvin"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig { minSdk = 21 }
}
