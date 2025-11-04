package sk.plomba.kotvin.auth.basickotvinauth.client.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import sk.plomba.kotvin.auth.basickotvinauth.shared.LoginRequest
import sk.plomba.kotvin.forms.SingleFieldStringResultValidator
import sk.plomba.kotvin.forms.ValidatorResult
import sk.plomba.kotvin.forms.fields.SimpleTextField
import sk.plomba.kotvin.forms.flows.continuousSingleFieldValidation
import sk.plomba.kotvin.forms.validators.EmailValidator
import sk.plomba.kotvin.utils.isEmailValid
import kotlin.math.log


class BasicLoginForm(
    val scope: CoroutineScope,
    val validateEmail: Boolean = false,
    val basicAuthDomain: BasicAuthDomain
) {

    val loginField = SimpleTextField()
    val passwordField = SimpleTextField()
    val submitButtonEnabled = MutableStateFlow(true)

    init {
        if(validateEmail){
            val emailValidator = EmailValidator(loginField)
            continuousSingleFieldValidation(loginField, emailValidator, scope)
            emailValidator.resultState.onEach { submitButtonEnabled.value = it.isValid }.launchIn(scope)
        }
    }

    fun submit() {
        scope.launch(Dispatchers.IO) {
            basicAuthDomain.login(
                LoginRequest(
                    loginField.valueState.value,
                    passwordField.valueState.value
                )
            )
        }
    }
}