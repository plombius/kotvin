package sk.plomba.kotvin.forms.flows

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import sk.plomba.kotvin.forms.SingleFieldValidator
import sk.plomba.kotvin.forms.ValidatedFormField
import sk.plomba.kotvin.forms.Validator
import sk.plomba.kotvin.forms.ValidatorResult

fun continuousMultiValidation(
    validators: List<Validator<*>>
): Flow<Boolean>{
    return validators.map { it.resultState }.allTrueFlow()
}

fun List<StateFlow<ValidatorResult<*>>>.allTrueFlow(): Flow<Boolean>{
    return combine(this) { it.all { it.isValid }}
}