package sk.plomba.kotvin.testapp.client.di

import sk.plomba.kotvin.auth.basickotvinauth.shared.generated.BasicLoginApiImpl
import sk.plomba.kotvin.networking.http.client.HttpExecutor
import sk.plomba.kotvin.networking.http.client.HttpExecutor2
import sk.plomba.kotvin.networking.http.client.P
import sk.plomba.kotvin.platform.PlatformContext
import sk.plomba.kotvin.storage.sql.runtime.getKotvinDbHandler
import sk.plomba.kotvin.testapp.client.achievements.data.AchievementHttpApiImpll
import sk.plomba.kotvin.testapp.client.achievements.data.AchievementRepository
import sk.plomba.kotvin.testapp.shared.generated.AchievementApiImpl
import sk.plomba.kotvin.testapp.shared.generated.AchievementDao
import kotlin.getValue
import kotlin.reflect.KFunction

class AppModule (
    platformContext: PlatformContext
){
    val httpExecutor by lazy { HttpExecutor("") }
    val httpExecutor2 by lazy { HttpExecutor2("") }
    val kotvinDbHandler by lazy {
        val db = getKotvinDbHandler(platformContext)
        db.connect("","", "")
        db
    }
    val achievementDao by lazy { AchievementDao(kotvinDbHandler) }

    val basicLoginApi by lazy { BasicLoginApiImpl(httpExecutor2) }

    val achievementApi by lazy { AchievementApiImpl(httpExecutor2) }
    val achievementRepository by lazy { AchievementRepository(achievementApi, achievementDao) }
}