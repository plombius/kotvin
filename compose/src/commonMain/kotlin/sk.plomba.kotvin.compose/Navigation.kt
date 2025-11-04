package sk.plomba.kotvin.compose

import androidx.compose.runtime.mutableStateListOf
import kotlinx.serialization.Serializable

@Serializable
open class NavigationRoute

class KotvinNavController(){
    val backstack = mutableStateListOf<NavigationRoute>()

    fun navigate(route: NavigationRoute){
        backstack.add(route)
    }

    fun pop(){
        backstack.removeAt(backstack.lastIndex)
    }
}