rootProject.name = "kotvin"

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        mavenLocal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}


include("networking:tcp")
include(":networking:http:client")
include(":networking:http:server")
include("networking:http:shared")
include("networking:http:kspProcessor")
include(":storage")
include("storage:sql")
include("testapp:client")

include("testapp:server")
include("networking:server")
include("networking:http:server")
include("testapp:shared")
include("storage:sql:kspProcessor")
include("storage:sql:runtime")
include(":forms")
include(":platform")
include(":compose")
include(":utils")
include(":auth:basickotvinauth:client")
include(":auth:basickotvinauth:server")
include(":auth:basickotvinauth:shared")
