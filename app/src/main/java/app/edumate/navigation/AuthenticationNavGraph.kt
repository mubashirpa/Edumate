package app.edumate.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import app.edumate.presentation.onboarding.OnboardingScreen
import app.edumate.presentation.resetPassword.ResetPasswordScreen
import app.edumate.presentation.signIn.SignInScreen
import app.edumate.presentation.signUp.SignUpScreen
import kotlinx.coroutines.CoroutineScope

fun NavGraphBuilder.authentication(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
) {
    val modifier =
        Modifier
            .fillMaxSize()
            .imePadding()
    val navigateToSignIn: () -> Unit = {
        navController.navigate(Screen.SignIn) {
            popUpTo(Screen.SignUp) { inclusive = true }
            launchSingleTop = true
        }
    }
    val navigateToHome: () -> Unit = {
        navController.popBackStack(Graph.Authentication, true)
        navController.navigate(Screen.Home(null))
    }

    navigation<Graph.Authentication>(startDestination = Screen.Onboarding) {
        composable<Screen.Onboarding> {
            OnboardingScreen(
                onNavigateToSignIn = {
                    navController.navigate(Screen.SignIn)
                },
                modifier = modifier,
            )
        }
        composable<Screen.SignIn> {
            SignInScreen(
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUp) {
                        popUpTo(Screen.SignIn) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToForgetPassword = { email ->
                    navController.navigate(Screen.ResetPassword(email = email))
                },
                onSignInComplete = {
                    navigateToHome()
                },
                modifier = modifier,
            )
        }
        composable<Screen.SignUp> {
            SignUpScreen(
                snackbarHostState = snackbarHostState,
                coroutineScope = coroutineScope,
                onNavigateToSignIn = navigateToSignIn,
                onSignUpComplete = { isVerified ->
                    if (isVerified) {
                        navigateToHome()
                    } else {
                        navigateToSignIn()
                    }
                },
                modifier = modifier,
            )
        }
        composable<Screen.ResetPassword> {
            ResetPasswordScreen(
                snackbarHostState = snackbarHostState,
                coroutineScope = coroutineScope,
                onResetPasswordComplete = navController::navigateUp,
                modifier = modifier,
            )
        }
    }
}
