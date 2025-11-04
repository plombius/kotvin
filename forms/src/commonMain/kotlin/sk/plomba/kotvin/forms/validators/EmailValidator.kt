package sk.plomba.kotvin.forms.validators

import kotlinx.coroutines.flow.StateFlow
import sk.plomba.kotvin.forms.FormField
import sk.plomba.kotvin.forms.SingleFieldStringResultValidator
import sk.plomba.kotvin.forms.ValidatedFormField
import sk.plomba.kotvin.forms.ValidatorResult
import sk.plomba.kotvin.utils.isEmailValid

class EmailValidator(field: ValidatedFormField<String, String>): SingleFieldStringResultValidator<String>(
    field, {
        if(it.isEmailValid()) {
            ValidatorResult(true)
        } else {
            ValidatorResult(false, "Invalid email")
        }
    }
)