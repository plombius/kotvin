package sk.plomba.kotvin.testapp.client.achievements.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import sk.plomba.kotvin.testapp.client.achievements.data.AchievementRepository
import sk.plomba.kotvin.testapp.client.achievements.domain.AchievementForm

class AddAchievementViewModel(
    val achievementRepository: AchievementRepository
): ViewModel() {

    val achievementForm = AchievementForm(viewModelScope)
    val name = MutableStateFlow("")
    val location = MutableStateFlow("")
    val datetime: MutableStateFlow<Long> = MutableStateFlow(0)
    val duration: MutableStateFlow<Long> = MutableStateFlow(0)
}