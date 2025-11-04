package sk.plomba.kotvin.forms.fields

import kotlinx.coroutines.flow.MutableStateFlow
import sk.plomba.kotvin.forms.FormField
import sk.plomba.kotvin.forms.ValidatedFormField
import sk.plomba.kotvin.forms.ValidatorResult

class SimpleTextField : ValidatedFormField<String, String>{

    override val valueState = MutableStateFlow("")
    override val errorState: MutableStateFlow<ValidatorResult<String>> =
        MutableStateFlow(ValidatorResult(true))
    val labelState: MutableStateFlow<String> = MutableStateFlow("ooonull")
}