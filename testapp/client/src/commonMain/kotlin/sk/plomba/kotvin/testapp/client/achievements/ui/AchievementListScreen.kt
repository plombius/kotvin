package sk.plomba.kotvin.testapp.client.achievements.ui
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import sk.plomba.kotvin.compose.KotvinNavController
import sk.plomba.kotvin.testapp.client.RouteAddAchievement
import sk.plomba.kotvin.testapp.client.achievements.viewmodel.AchievementListViewModel
import sk.plomba.kotvin.testapp.shared.Achievement

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementListScreen(
    viewModel: AchievementListViewModel,
    navController: KotvinNavController
){
    val achievementsLocal = viewModel.achievementsLocal.collectAsState()
    val achievementsRemote = viewModel.achievementsRemote.collectAsState()

    var localChecked = remember { true }
    var remoteChecked = remember { true }

    Column {
        Spacer(Modifier.height(32.dp))
        Row {
            Button(
                onClick = {
                    navController.navigate(RouteAddAchievement)
                }
            ){
                Text("add achievement")
            }
            Button(
                onClick = {
                    viewModel.load()
                }
            ){
                Text("load")
            }
        }
        Row {
            Checkbox(
                checked = localChecked,
                onCheckedChange = {localChecked = it}
            )
            Text("show local")
        }
        Row {
            Checkbox(
                checked = remoteChecked,
                onCheckedChange = {remoteChecked = it}
            )
            Text("show remote")
        }
        LazyColumn {
            if(remoteChecked) {
                items(achievementsRemote.value) {
                    AchievementRow(it, true)
                }
            }
            if(localChecked) {
                items(achievementsLocal.value) {
                    AchievementRow(it, false)
                }
            }
        }


        // Create a DatePicker state
        val datePickerState = rememberDatePickerState()

        // Create a formatter
        val dateFormatter = remember { DatePickerDefaults.dateFormatter() }
        DatePicker(
            state = datePickerState,
            dateFormatter = dateFormatter,
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer(
                    scaleX = 1f,
                    scaleY = 1f,
                    transformOrigin = TransformOrigin(0f, 0f)
                )
        )
    }
}

@Composable
fun AchievementRow(achievement: Achievement, isRemote: Boolean){
    Column (
        Modifier.background(color = if(isRemote) Color.Blue else Color.Green)
            .padding(16.dp))
    {
        Text(text = if (isRemote) "remote" else "local")
        Spacer(Modifier.height(16.dp))
        Text(
            color = Color.White,
            text = "Name: ${achievement.name}, location: ${achievement.location}, datetime: ${achievement.dateTime}, duration: ${achievement.duration}, id: ${achievement.id}"
        )
    }
}