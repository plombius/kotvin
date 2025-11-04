package sk.plomba.kotvin.compose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import kotlin.reflect.KClass

fun <VM: ViewModel> viewModelFactory(initializer: () -> VM): ViewModelProvider.Factory{
    return object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
            return initializer() as T
        }
    }
}