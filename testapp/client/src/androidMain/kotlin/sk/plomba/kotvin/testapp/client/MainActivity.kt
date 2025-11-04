package sk.plomba.kotvin.testapp.client

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import sk.plomba.kotvin.platform.PlatformContext
import sk.plomba.kotvin.storage.sql.runtime.AndroidKotvinDbHandler
import sk.plomba.kotvin.storage.sql.runtime.getKotvinDbHandler
import sk.plomba.kotvin.testapp.client.di.AppModule
import sk.plomba.kotvin.testapp.shared.generated.AchievementDao

class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val appModule: AppModule = AppModule(PlatformContext(this))

        setContent {
            // Remove when https://issuetracker.google.com/issues/364713509 is fixed
            LaunchedEffect(isSystemInDarkTheme()) {
                enableEdgeToEdge()
            }

            App(appModule)
        }
    }
}