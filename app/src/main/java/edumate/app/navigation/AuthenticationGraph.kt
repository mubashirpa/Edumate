package edumate.app.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import edumate.app.presentation.get_started.GetStartedScreen
import edumate.app.presentation.login.screen.LoginScreen
import edumate.app.presentation.recover.screen.RecoverScreen
import edumate.app.presentation.register.screen.RegisterScreen
import kotlinx.coroutines.CoroutineScope

fun NavGraphBuilder.authentication(
    navController: NavController,
    rootSnackbarHostState: SnackbarHostState,
    rootSnackbarScope: CoroutineScope,
) {
    navigation(
        startDestination = Screen.GetStartedScreen.route,
        route = Routes.Graph.AUTHENTICATION,
    ) {
        composable(route = Screen.GetStartedScreen.route) {
            GetStartedScreen(
                navigateToLogin = {
                    navController.navigate(Screen.LoginScreen.route)
                },
            )
        }
        composable(route = Screen.LoginScreen.route) {
            LoginScreen(
                navigateToRegister = {
                    navController.navigate(Screen.RegisterScreen.route) {
                        popUpTo(Screen.LoginScreen.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateToRecover = { email ->
                    navController.navigate(
                        "${Screen.RecoverScreen.route}?${Routes.Args.RECOVER_EMAIL}=$email",
                    )
                },
                onLoginSuccess = {
                    navController.popBackStack(Routes.Graph.AUTHENTICATION, true)
                    navController.navigate(Screen.HomeScreen.route)
                },
            )
        }
        composable(route = Screen.RegisterScreen.route) {
            RegisterScreen(
                navigateToLogin = {
                    navController.navigate(Screen.LoginScreen.route) {
                        popUpTo(Screen.RegisterScreen.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onRegisterSuccess = {
                    navController.popBackStack(Routes.Graph.AUTHENTICATION, true)
                    navController.navigate(Screen.HomeScreen.route)
                },
            )
        }
        composable(
            route = "${Screen.RecoverScreen.route}${Routes.Args.RECOVER_SCREEN}",
            arguments =
                listOf(
                    navArgument(Routes.Args.RECOVER_EMAIL) {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                ),
        ) {
            RecoverScreen(
                rootSnackbarHostState = rootSnackbarHostState,
                rootSnackbarScope = rootSnackbarScope,
                onPasswordResetEmailSent = {
                    navController.navigateUp()
                },
            )
        }
    }
}
