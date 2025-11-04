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

            api(libs.androidx.lifecycle.viewmodel.nav3)
            api(libs.androidx.material3.adaptive.nav3)
            api(libs.androidx.nav3.ui)
            api(libs.androidx.nav3.ui)
            api(libs.androidx.material3.adaptive)
            api(libs.kotlinx.json)
            api(libs.kotlinx.core)

        }


        androidMain.dependencies {
        }

        jvmMain.dependencies {
        }
    }
}

android {
    namespace = "sk.plomba.kotvin.compose"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}