package sk.plomba.kotvin.testapp.client.achievements.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import sk.plomba.kotvin.forms.fields.SimpleTextField
import sk.plomba.kotvin.forms.flows.continuousMultiValidation
import sk.plomba.kotvin.forms.flows.continuousSingleFieldValidation
import sk.plomba.kotvin.forms.validators.EmailValidator

class AchievementForm (val scope: CoroutineScope){

    val emialfield = SimpleTextField()
    val validator = EmailValidator(emialfield)
    val jobes = continuousSingleFieldValidation(emialfield, validator, scope)


    val emialfield2 = SimpleTextField()
    val validator2 = EmailValidator(emialfield2)
    val jobes2 = continuousSingleFieldValidation(emialfield2, validator2, scope)

    val floww = continuousMultiValidation(
        listOf(
            validator, validator2
        )
    )
    init {
        floww.launchIn(scope)
    }

    fun stop(){
        jobes.forEach { it.cancel() }
    }
}