plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)          // <-- use the Android Library plugin

    //alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.vanniktech.mavenPublish)
    alias (libs.plugins.kotlinx.serialization)


}


group = "sk.plomba.kotvin.networking.http.shared"

kotlin {
    jvm()
    androidTarget()
    /*androidLibrary {
        namespace = "sk.plomba.kotvin"
        compileSdk = libs.versions.android.compileSdk.get().toInt()

    }*/



    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.json)
            api(libs.ktor.serialization.kotlinx.json)

            api(libs.ktor.http)

        }
    }
}


android {                                         // <-- top-level, not inside kotlin
    namespace = "sk.plomba.kotvin"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig { minSdk = 21 }
}


