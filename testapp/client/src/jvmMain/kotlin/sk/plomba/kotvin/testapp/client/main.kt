package sk.plomba.kotvin.testapp.client

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.runBlocking
import sk.plomba.kotvin.platform.PlatformContext
import sk.plomba.kotvin.testapp.client.di.AppModule
import kotlin.reflect.full.*


fun main() = application {
    val appModule: AppModule = AppModule(PlatformContext())
    Window(
        onCloseRequest = ::exitApplication,
        title = "achievements",
    ) {
        App(appModule)
    }
}