package edumate.app.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import edumate.app.navigation.EdumateNavHost
import edumate.app.navigation.Routes

@Composable
fun EdumateApp(
    isLoggedIn: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val navController = rememberNavController()
        if (isLoggedIn) {
            EdumateNavHost(
                navController = navController,
                startDestination = Routes.Screen.HOME_SCREEN
            )
        } else {
            EdumateNavHost(navController = navController)
        }
    }
}