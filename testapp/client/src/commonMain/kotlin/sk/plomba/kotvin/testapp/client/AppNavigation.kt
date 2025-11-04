package sk.plomba.kotvin.testapp.client

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import sk.plomba.kotvin.testapp.shared.Achievement
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import sk.plomba.kotvin.compose.KotvinNavController
import sk.plomba.kotvin.compose.NavigationRoute
import sk.plomba.kotvin.compose.viewModelFactory
import sk.plomba.kotvin.testapp.client.achievements.ui.AchievementListScreen
import sk.plomba.kotvin.testapp.client.achievements.ui.AddAchievementScreen
import sk.plomba.kotvin.testapp.client.achievements.viewmodel.AchievementListViewModel
import sk.plomba.kotvin.testapp.client.achievements.viewmodel.AddAchievementViewModel
import sk.plomba.kotvin.testapp.client.di.AppModule


@Composable
fun App(
    appModule: AppModule,
){

    val navController = remember { KotvinNavController() }
    navController.navigate(RouteListAchievements)

    NavDisplay(
        backStack = navController.backstack,
        entryProvider = entryProvider {
            entry <RouteListAchievements>{
                AchievementListScreen(
                    viewModel(
                        factory = viewModelFactory{
                            AchievementListViewModel(appModule.achievementRepository)
                        }
                    ),
                    navController
                )
            }
            entry <RouteAddAchievement>{
                AddAchievementScreen(
                    viewModel(
                        factory = viewModelFactory{
                            AddAchievementViewModel(appModule.achievementRepository)
                        }
                    ),
                    navController
                )
            }
        },
        entryDecorators = listOf(
            //rememberSceneSetupNavEntryDecorator(),

            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        )
    )


    Column {
        Spacer(Modifier.height(50.dp))







        LazyColumn {

            /*items(items = achievementDao.findAll()) {
                Text("${it.id} - ${it.name}")
            }*/


            items(items = listOf<Achievement>(
                //Achievement(0, "uuu", "eee", "aaa", 0, 0)
            )) {
                Text("${it.id} - ${it.name}")
            }
        }
    }
}

data object RouteListAchievements: NavigationRoute()
data object RouteAddAchievement: NavigationRoute()