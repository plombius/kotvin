package sk.plomba.kotvin.testapp.server

import io.ktor.http.HttpStatusCode
import io.ktor.http.HttpMethod

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import sk.plomba.kotvin.networking.http.server.simpleKotvinHttpServerSetup
import sk.plomba.kotvin.networking.http.server.sk.plomba.kotvin.networking.http.server.mountAnnotatedApi
import sk.plomba.kotvin.networking.http.shared.DefaultHttpErrorDto
import sk.plomba.kotvin.testapp.shared.AchievementApi

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)


fun Application.module() {

    simpleKotvinHttpServerSetup()

    routing {
        get("/") {
            val x = 5 / 0
            //throw Exception()
            call.respondText("Hello, uuu! $x")
            val u = call.request.queryParameters["aa"]
            //call.respond(HttpStatusCode.OK,  DefaultHttpErrorDto(500, "zle"),)
        }

        mountAnnotatedApi(AchievementApiServerImpl() as AchievementApi)
    }
}


