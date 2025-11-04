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

            api(project(":platform"))
            api(project(":compose"))
            api(project(":utils"))

        }


        androidMain.dependencies {


        }

        jvmMain.dependencies {
        }
    }
}

android {
    namespace = "sk.plomba.kotvin.forms"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}