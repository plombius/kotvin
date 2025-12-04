package sk.plomba.kotvin.testapp.client.achievements.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import sk.plomba.kotvin.testapp.client.achievements.data.AchievementRepository
import sk.plomba.kotvin.testapp.shared.Achievement

class AchievementListViewModel(
    val achievementRepository: AchievementRepository
): ViewModel() {

    val achievementsLocal: MutableStateFlow<List<Achievement>> = MutableStateFlow(listOf())
    val achievementsRemote: MutableStateFlow<List<Achievement>> = MutableStateFlow(listOf())

    val localChecked = MutableStateFlow(true)
    val remoteChecked = MutableStateFlow(true)


    fun load() {
        viewModelScope.launch(Dispatchers.IO) {
            achievementsLocal.value = achievementRepository.getAllLocal()
            //achievementsRemote.value = achievementRepository.getAllRemote().data?: listOf()
        }
    }

    fun onLocalCheckChanged(checked: Boolean){
        localChecked.value = checked
    }

    fun onRemoteCheckChanged(checked: Boolean){
        remoteChecked.value = checked
    }

    fun add(){
        viewModelScope.launch {
            achievementRepository.saveAchievementLocal(Achievement(name = "jozo", location = "tu", dateTime = 5, duration = 25))
        }
    }
}