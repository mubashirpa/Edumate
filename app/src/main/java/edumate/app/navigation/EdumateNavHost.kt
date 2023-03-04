package edumate.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import edumate.app.presentation.create_class.screen.CreateClassScreen
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
        composable(route = Screen.HomeScreen.route) {
            HomeScreen(
                navigateToCreateRoom = {
                    navController.navigate(Screen.CreateClassScreen.route)
                }
            )
        }
        composable(route = Screen.CreateClassScreen.route) {
            CreateClassScreen(
                navigateToRoom = {
                    navController.navigateUp()
                },
                onBackPressed = {
                    navController.navigateUp()
                }
            )
        }
    }
}