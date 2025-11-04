package sk.plomba.kotvin.auth.basickotvinauth.client.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import sk.plomba.kotvin.auth.basickotvinauth.client.data.BasicAuthRepository
import sk.plomba.kotvin.auth.basickotvinauth.shared.ApiKey
import sk.plomba.kotvin.auth.basickotvinauth.shared.LoginRequest
import sk.plomba.kotvin.auth.basickotvinauth.shared.RegisterRequest
import sk.plomba.kotvin.networking.http.client.HttpExecutor

class BasicAuthDomain (val repository: BasicAuthRepository){

    enum class LoginResult{
        UNVERIFIED_ACCOUNT, FAILED, OK
    }

    enum class RegisterResult{
        INPUT_IS_NULL, ALREADY_EXISTS, UNVERIFIED_ACCOUNT, FAILED, OK
    }
    val apikeyState: MutableStateFlow<ApiKey?> = MutableStateFlow(null)

    suspend fun login(
        loginRequest: LoginRequest
    ): LoginResult{
        val result = repository.login(loginRequest)
        if(result.isSuccess && result.data?.apiKey != null){
            apikeyState.value = result.data?.apiKey
            return LoginResult.OK
        } else {
            return LoginResult.FAILED
        }
    }

    suspend fun register(
        registerRequest: RegisterRequest
    ): RegisterResult{
        if(registerRequest.email == null && registerRequest.username == null){
            return RegisterResult.INPUT_IS_NULL
        } else {
            val result = repository.register(registerRequest)
            if(result.isSuccess && result.data?.apiKey != null){
                apikeyState.value = result.data?.apiKey
                return RegisterResult.OK
            } else {
                return RegisterResult.FAILED
            }
        }
    }

    fun logout(){
        apikeyState.value = null
    }

    fun getHttpExecutor(): HttpExecutor{

        return HttpExecutor("")
    }
}