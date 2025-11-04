package sk.plomba.kotvin.forms

import kotlinx.coroutines.flow.MutableStateFlow

interface FormField<VALUE>{
    val valueState: MutableStateFlow<VALUE>
}

interface ValidatedFormField<VALUE, ERROR>: FormField<VALUE>{
    val errorState: MutableStateFlow<ValidatorResult<ERROR>>
}