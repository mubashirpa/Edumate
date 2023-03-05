package edumate.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import edumate.app.presentation.people.screen.ClassworkScreen
import edumate.app.presentation.people.screen.PeopleScreen
import edumate.app.presentation.people.screen.StreamScreen

@Composable
fun ClassDetailsNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.StreamScreen.route,
        modifier = modifier
    ) {
        composable(route = Screen.StreamScreen.route) {
            StreamScreen()
        }
        composable(route = Screen.ClassworkScreen.route) {
            ClassworkScreen()
        }
        composable(route = Screen.PeopleScreen.route) {
            PeopleScreen()
        }
    }
}