plugins {

    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeHotReload) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.vanniktech.mavenPublish) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinx.serialization) apply false
    alias(libs.plugins.kspPlugin) apply false
    alias(libs.plugins.android.lint) apply false

}

allprojects {
    version = "0.0.1"
}