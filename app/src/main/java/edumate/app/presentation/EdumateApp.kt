package edumate.app.presentation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import edumate.app.navigation.EdumateNavHost
import edumate.app.navigation.Routes

@Composable
fun EdumateApp(isLoggedIn: Boolean) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val startDestination =
        if (isLoggedIn) Routes.Screen.HOME_SCREEN else Routes.Graph.AUTHENTICATION

    Scaffold(
        modifier =
            Modifier
                .fillMaxSize()
                .imePadding(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        contentWindowInsets = WindowInsets(0),
    ) { innerPadding ->
        EdumateContent(
            navController = navController,
            modifier =
                Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)),
            startDestination = startDestination,
            snackbarHostState = snackbarHostState,
        )
    }
}

@Composable
private fun EdumateContent(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Routes.Graph.AUTHENTICATION,
    snackbarHostState: SnackbarHostState,
) {
    EdumateNavHost(
        navController = navController,
        modifier = modifier,
        startDestination = startDestination,
        snackbarHostState = snackbarHostState,
    )
}
