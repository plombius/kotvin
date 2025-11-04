package sk.plomba.kotvin.testapp.server

import sk.plomba.kotvin.networking.http.shared.DefaultHttpErrorDto
import sk.plomba.kotvin.networking.http.shared.KotvinHttpCallResponse
import sk.plomba.kotvin.testapp.shared.Achievement
import sk.plomba.kotvin.testapp.shared.AchievementApi
import sk.plomba.kotvin.testapp.shared.SaveResultDto

class AchievementApiServerImpl: AchievementApi {
    override suspend fun getAchievements(searchQuery: String?, sgf: String?): KotvinHttpCallResponse<List<Achievement>, DefaultHttpErrorDto> {
        return KotvinHttpCallResponse(data = listOf(Achievement(name = "plomba", location = "tu", dateTime = 55, duration = 85)), isSuccess = true)
    }

    override suspend fun save(achievement: Achievement): KotvinHttpCallResponse<SaveResultDto, DefaultHttpErrorDto> {
        return KotvinHttpCallResponse(SaveResultDto(true, null), isSuccess = true)
    }
}