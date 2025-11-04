package sk.plomba.kotvin.testapp.client.achievements.data

import sk.plomba.kotvin.networking.http.client.HttpExe
import sk.plomba.kotvin.networking.http.client.HttpExecutor
import sk.plomba.kotvin.networking.http.client.HttpExecutor2
import sk.plomba.kotvin.networking.http.shared.DefaultHttpErrorDto
import sk.plomba.kotvin.networking.http.shared.KotvinHttpCallResponse
import sk.plomba.kotvin.testapp.shared.Achievement
import sk.plomba.kotvin.testapp.shared.AchievementApi
import sk.plomba.kotvin.testapp.shared.SaveResultDto
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.typeOf


/*suspend inline fun <reified REQUEST, reified RESPONSE, reified ERROR> call(
    fn: KFunction<*>,
    baseApiUrl: String,
    vararg args: Any? suspend inline ()
): KotvinHttpCallResponse<RESPONSE, ERROR>*/
class AchievementHttpApiImpll(
    val httpExecutor2: HttpExe,
    private val baseApiUrl: String = ""
) : AchievementApi {

    @Suppress("UNCHECKED_CAST")
    override suspend fun getAchievements(searchQuery: String?,sdsearchQuery: String?,): KotvinHttpCallResponse<List<Achievement>, DefaultHttpErrorDto> =
        httpExecutor2.call<List<Achievement>, DefaultHttpErrorDto>(AchievementApi::getAchievements, baseApiUrl, arrayOf(searchQuery, sdsearchQuery), responseType = typeOf<List<Achievement>>(), errorType = typeOf<DefaultHttpErrorDto>()) as KotvinHttpCallResponse<List<Achievement>, DefaultHttpErrorDto>

    @Suppress("UNCHECKED_CAST")
    override suspend fun save(achievement: Achievement): KotvinHttpCallResponse<SaveResultDto, DefaultHttpErrorDto> =
        httpExecutor2.call<Unit, DefaultHttpErrorDto>(AchievementApi::save, baseApiUrl, arrayOf(achievement), responseType = typeOf<Unit>(), errorType = typeOf<DefaultHttpErrorDto>()) as KotvinHttpCallResponse<SaveResultDto, DefaultHttpErrorDto>
}