package app.edumate.presentation.main.components

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import app.edumate.navigation.EdumateNavHost
import org.koin.compose.KoinContext

@Composable
fun EdumateApp(
    navController: NavHostController,
    startDestination: Any,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    KoinContext {
        EdumateNavHost(
            navController = navController,
            snackbarHostState = snackbarHostState,
            modifier = modifier,
            startDestination = startDestination,
        )
    }
}
