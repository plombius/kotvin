plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)

}


kotlin {

    jvm(){


    }
    androidTarget()

    sourceSets {
        commonMain.dependencies {

        }


        androidMain.dependencies {


        }

        jvmMain.dependencies {
        }
    }
}

android {
    namespace = "sk.plomba.kotvin.utils"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}