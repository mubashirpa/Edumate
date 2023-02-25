package edumate.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import edumate.app.presentation.home.screen.HomeScreen

@Composable
fun EdumateNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.Graph.AUTHENTICATION
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        authentication(navController)
        composable(route = Routes.Screen.HOME_SCREEN) {
            HomeScreen()
        }
    }
}