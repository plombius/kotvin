package sk.plomba.kotvin.networking.http.server

import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import org.slf4j.event.Level
import sk.plomba.kotvin.networking.http.shared.DefaultHttpErrorDto
import io.ktor.server.response.*


fun Application.simpleKotvinHttpServerSetup(){

    install(ContentNegotiation){
        json()
    }

    install(StatusPages){
        status(HttpStatusCode.MethodNotAllowed){
            call.respond(HttpStatusCode.OK,  DefaultHttpErrorDto("grajnok", 500),)
        }
        exception<Throwable>{ call, cause ->
            call.respond(HttpStatusCode.OK,  DefaultHttpErrorDto(cause.message?:"unknown error", 500),)
        }
    }

    install(CallLogging){
        level = Level.DEBUG
    }
}
