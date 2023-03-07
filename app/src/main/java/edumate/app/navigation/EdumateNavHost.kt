package edumate.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import edumate.app.presentation.class_details.screen.ClassDetailsScreen
import edumate.app.presentation.create_class.screen.CreateClassScreen
import edumate.app.presentation.home.screen.HomeScreen
import edumate.app.presentation.join_class.screen.JoinClassScreen

@Composable
fun EdumateNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
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
                navigateToClassDetails = {
                    navController.navigate(Screen.ClassDetailsScreen.route)
                },
                navigateToCreateClass = {
                    navController.navigate(Screen.CreateClassScreen.route)
                },
                navigateToJoinClass = {
                    navController.navigate(Screen.JoinClassScreen.route)
                }
            )
        }
        composable(route = Screen.CreateClassScreen.route) {
            CreateClassScreen(
                navigateToClassDetails = { courseId ->
                    navController.navigate(Screen.ClassDetailsScreen.route) {
                        popUpTo(Screen.CreateClassScreen.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onBackPressed = {
                    navController.navigateUp()
                }
            )
        }
        composable(route = Screen.ClassDetailsScreen.route) {
            ClassDetailsScreen(
                onBackPressed = {
                    navController.navigateUp()
                }
            )
        }
        composable(route = Screen.JoinClassScreen.route) {
            JoinClassScreen(
                navigateToClassDetails = { courseId ->
                    navController.navigate(Screen.ClassDetailsScreen.route) {
                        popUpTo(Screen.JoinClassScreen.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToProfile = {
                    // TODO("Not yet implemented")
                },
                onBackPressed = {
                    navController.navigateUp()
                }
            )
        }
    }
}