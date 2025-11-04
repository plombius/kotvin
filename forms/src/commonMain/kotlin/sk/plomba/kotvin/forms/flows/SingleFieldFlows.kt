package sk.plomba.kotvin.forms.flows

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import sk.plomba.kotvin.forms.SingleFieldValidator
import sk.plomba.kotvin.forms.ValidatedFormField
import sk.plomba.kotvin.forms.Validator

fun <VALUE, ERROR> continuousSingleFieldValidation(
    field: ValidatedFormField<VALUE, ERROR>,
    validator: SingleFieldValidator<VALUE, ERROR>,
    scope: CoroutineScope
): List<Job>{
    return listOf(
        field.valueState.onEach { validator.validate() }.launchIn(scope),
        validator.resultState.onEach { field.errorState.value = it }.launchIn(scope)
    )
}