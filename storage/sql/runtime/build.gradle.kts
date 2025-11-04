@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)

}

kotlin {

    jvm()
    androidTarget()
    /*iosX64()
    iosArm64()
    iosSimulatorArm64()
    wasmJs()*/

    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>().configureEach {
        compilations["main"].cinterops {
            val sqlite3 by creating {
                defFile(project.file("src/iosMain/c_interop/sqlite3.def"))
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            //api(project(":storage:sql:kspProcessor"))
            implementation(project(":platform"))
        }


        androidMain.dependencies {
        }

        jvmMain.dependencies {
        }

        /*wasmJsMain.dependencies {
            implementation(npm("sql.js", "1.11.0"))
        }*/
    }
}

dependencies {

}

android {
    namespace = "sk.plomba.kotvin.storage.sql.runtime"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}
