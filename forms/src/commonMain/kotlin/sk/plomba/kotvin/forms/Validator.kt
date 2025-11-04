package sk.plomba.kotvin.forms

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ValidatorResult<INFO> (
    val isValid: Boolean,
    val resultInfo: INFO? = null
)

interface Validator<RESULT_INFO>{
    fun validate()
    val resultState: StateFlow<ValidatorResult<RESULT_INFO>>

}

abstract class ValidatorAbstract<RESULT_INFO>: Validator<RESULT_INFO>{

    override fun validate() {
        _resultState.value =  validateSingleInternal()
    }
    abstract fun validateSingleInternal(): ValidatorResult<RESULT_INFO>

    private val _resultState: MutableStateFlow<ValidatorResult<RESULT_INFO>> = MutableStateFlow(
        ValidatorResult(true))
    override val resultState = _resultState.asStateFlow()
}


open class SingleFieldValidator<INPUT_VALUE, RESULT_INFO>(
    val field: ValidatedFormField<INPUT_VALUE, RESULT_INFO>,
    val validation: (INPUT_VALUE) -> ValidatorResult<RESULT_INFO>
): ValidatorAbstract<RESULT_INFO>() {
    override fun validateSingleInternal(): ValidatorResult<RESULT_INFO> {
        return validation(field.valueState.value)
    }
}

open class SingleFieldStringResultValidator<INPUT_VALUE>(
    field: ValidatedFormField<INPUT_VALUE, String>,
    validation: (INPUT_VALUE) -> ValidatorResult<String>
): SingleFieldValidator<INPUT_VALUE, String>(
    field, validation
)


