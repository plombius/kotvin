plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kspPlugin)

}


kotlin {

    jvm(){


    }
    androidTarget()

    sourceSets {
        commonMain.dependencies {

            implementation(project(":platform"))
            implementation(project(":utils"))
            implementation(project(":networking:http:client"))

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

ksp {
    arg("httpapi.mode", "index") // default; actually generate files here
}

android {
    namespace = "sk.plomba.kotvin.forms"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}