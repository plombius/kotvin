package sk.plomba.kotvin.testapp.shared

import io.ktor.http.HttpMethod
import sk.plomba.kotvin.networking.http.shared.Body
import sk.plomba.kotvin.networking.http.shared.DefaultHttpErrorDto
import sk.plomba.kotvin.networking.http.shared.Endpoint
import sk.plomba.kotvin.networking.http.shared.HttpApi
import sk.plomba.kotvin.networking.http.shared.Query
import sk.plomba.kotvin.networking.http.shared.KotvinHttpCallResponse

interface AchievementApi : HttpApi{

    @Endpoint("GET", "/getall")
    suspend fun getAchievements(
        @Query(name = "searchQuery", required = false) searchQuery: String? = null,
        @Query(name = "searchQuery", required = false) searchQudsery: String? = null,
    ): KotvinHttpCallResponse<List<Achievement>, DefaultHttpErrorDto>

    @Endpoint("POST", "/save")
    suspend fun save(
        @Body achievement: Achievement,
    ): KotvinHttpCallResponse<SaveResultDto, DefaultHttpErrorDto>
}
