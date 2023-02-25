package edumate.app.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import edumate.app.presentation.get_started.GetStartedScreen
import edumate.app.presentation.login.screen.LoginScreen
import edumate.app.presentation.register.screen.RegisterScreen

fun NavGraphBuilder.authentication(navController: NavController) {
    navigation(
        startDestination = Routes.Screen.GET_STARTED_SCREEN,
        route = Routes.Graph.AUTHENTICATION
    ) {
        composable(route = Routes.Screen.GET_STARTED_SCREEN) {
            GetStartedScreen(
                navigateToLogin = {
                    navController.navigate(Routes.Screen.LOGIN_SCREEN)
                }
            )
        }
        composable(route = Routes.Screen.LOGIN_SCREEN) {
            LoginScreen(
                navigateToRegister = {
                    navController.navigate(Routes.Screen.REGISTER_SCREEN) {
                        popUpTo(Routes.Screen.LOGIN_SCREEN) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToRecover = {
                },
                onLoginSuccess = {
                    navController.popBackStack()
                    navController.navigate(Routes.Screen.HOME_SCREEN)
                }
            )
        }
        composable(route = Routes.Screen.REGISTER_SCREEN) {
            RegisterScreen(
                navigateToLogin = {
                    navController.navigate(Routes.Screen.LOGIN_SCREEN) {
                        popUpTo(Routes.Screen.REGISTER_SCREEN) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onRegisterSuccess = {
                    navController.popBackStack()
                    navController.navigate(Routes.Screen.HOME_SCREEN)
                }
            )
        }
    }
}