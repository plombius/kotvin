import com.google.common.collect.Range.all
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerArgumentsProducer.ArgumentType.Companion.all
import java.util.random.RandomGeneratorFactory.all

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


            implementation(project(":networking:http:client"))
            implementation(project(":forms"))
            api(project(":auth:basickotvinauth:shared"))

        }


        androidMain.dependencies {


        }

        jvmMain.dependencies {
        }
    }
    //sourceSets["all"].kotlin.srcDir("build/generated/ksp/")

}



dependencies{
    add("kspCommonMainMetadata", project(":networking:http:kspProcessor"))
    add("kspJvm", project(":networking:http:kspProcessor"))
    add("kspAndroid", project(":networking:http:kspProcessor"))
}


ksp {
    arg("packages", "sk.plomba.kotvin.auth.basickotvinauth.shared") // default; actually generate files here
}


android {
    namespace = "sk.plomba.kotvin.forms"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}