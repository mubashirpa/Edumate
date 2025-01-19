package app.edumate.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import app.edumate.presentation.profile.ProfileScreen

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EdumateNavHost(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    startDestination: Any = Graph.Authentication,
) {
    val coroutineScope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        authentication(
            navController = navController,
            snackbarHostState = snackbarHostState,
            coroutineScope = coroutineScope,
        )
        composable<Screen.Home> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Hello World!")
                Button(onClick = {
                    navController.navigate(Screen.Profile)
                }) {
                    Text(text = "Go to Profile")
                }
            }
        }
        composable<Screen.Profile> {
            ProfileScreen(
                onNavigateUp = navController::navigateUp,
                onSignOutComplete = {
                    navController.navigate(Graph.Authentication) {
                        popUpTo(Graph.Authentication) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
            )
        }
    }
}
