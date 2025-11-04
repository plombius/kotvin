package sk.plomba.kotvin.testapp.client.achievements.data

import sk.plomba.kotvin.networking.http.shared.DefaultHttpErrorDto
import sk.plomba.kotvin.networking.http.shared.KotvinHttpCallResponse
import sk.plomba.kotvin.testapp.shared.Achievement
import sk.plomba.kotvin.testapp.shared.AchievementApi
import sk.plomba.kotvin.testapp.shared.SaveResultDto
import sk.plomba.kotvin.testapp.shared.generated.AchievementDao


class AchievementRepository(
    val achievementApi: AchievementApi,
    val achievementDao: AchievementDao
) {

    suspend fun saveAchievementRemote(achievement: Achievement): KotvinHttpCallResponse<SaveResultDto, DefaultHttpErrorDto>{
        return achievementApi.save(achievement)
    }

    suspend fun saveAchievementLocal(achievement: Achievement): Int{
        return achievementDao.insert(achievement)
    }

    suspend fun getAllRemote(): KotvinHttpCallResponse<List<Achievement>, DefaultHttpErrorDto>{
        return achievementApi.getAchievements()
    }

    suspend fun getAllLocal(): List<Achievement>{
        return achievementDao.findAll()
    }
}
