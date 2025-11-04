package sk.plomba.kotvin.testapp.client.achievements.ui
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import sk.plomba.kotvin.compose.KotvinNavController
import sk.plomba.kotvin.compose.NavigationRoute
import sk.plomba.kotvin.compose.viewModelFactory
import sk.plomba.kotvin.forms.fields.SimpleTextField
import sk.plomba.kotvin.testapp.client.RouteAddAchievement
import sk.plomba.kotvin.testapp.client.RouteListAchievements
import sk.plomba.kotvin.testapp.client.achievements.viewmodel.AchievementListViewModel
import sk.plomba.kotvin.testapp.client.achievements.viewmodel.AddAchievementViewModel

data object RouteA: NavigationRoute()
data object RouteB: NavigationRoute()
@Composable
fun AddAchievementScreen(
    viewModel: AddAchievementViewModel,
    navController: KotvinNavController
){
    val navvController = remember { KotvinNavController() }
    navvController.navigate(RouteA)


    Column {
        Spacer(Modifier.height(32.dp))
        Text("EEEEEEEEEEE")


        NavDisplay(
            backStack = navvController.backstack,
            entryProvider = entryProvider {
                entry<RouteA> {
                    Button(onClick = { navvController.navigate(RouteB) }) {
                        Text("A"+ navvController.backstack.size)
                    }
                }
                entry<RouteB> {
                    Text("B"+ navvController.backstack.size)
                }
            },
            entryDecorators = listOf(
                //rememberSceneSetupNavEntryDecorator(),

                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            )
        )


        val navvvController = remember { KotvinNavController() }
        navvvController.navigate(RouteA)

        NavDisplay(
            backStack = navvvController.backstack,
            entryProvider = entryProvider {
                entry<RouteA> {
                    Button(onClick = {
                        navvvController.navigate(RouteB)
                    }) {
                        Text("A"+ navvvController.backstack.size)
                    }
                }
                entry<RouteB> {
                    Text("B"+ navvvController.backstack.size)
                }
            },
            entryDecorators = listOf(
                //rememberSceneSetupNavEntryDecorator(),

                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            )
        )

        Button(onClick = {
            viewModel.achievementForm.stop()
        }) {
            Text("IIIIII")
        }

        TxtF(viewModel.achievementForm.emialfield)
        TxtF(viewModel.achievementForm.emialfield2)
        val uiu = viewModel.achievementForm.floww.collectAsState(true)
        Button(enabled = uiu.value, onClick = {}){
            Text("ujuju")
        }
    }
}

@Composable
fun TxtF(simpleTextField: SimpleTextField){

    val uuu = simpleTextField.errorState.collectAsState()
    val vvv = simpleTextField.valueState.collectAsState()

    TextField(value = vvv.value, onValueChange = {
        simpleTextField.valueState.value = it
    }, label = {Text("nihil")}, supportingText = {Text(text = uuu.value.resultInfo?:"dobge")})

}