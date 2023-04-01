package edumate.app.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import edumate.app.navigation.EdumateNavHost
import edumate.app.navigation.Routes
import edumate.app.presentation.components.EdumateDrawerContent
import kotlinx.coroutines.launch

@Composable
fun EdumateApp(
    isLoggedIn: Boolean
) {
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerContent = {
            EdumateDrawerContent(
                onProfileClicked = {
                    coroutineScope.launch { drawerState.close() }
                }
            )
        },
        drawerState = drawerState
    ) {
        EdumateAppContent(
            isLoggedIn = isLoggedIn,
            drawerState = drawerState
        )
    }
}

@Composable
private fun EdumateAppContent(
    isLoggedIn: Boolean,
    drawerState: DrawerState
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val navController = rememberNavController()
        if (isLoggedIn) {
            EdumateNavHost(
                navController = navController,
                startDestination = Routes.Screen.HOME_SCREEN,
                drawerState = drawerState
            )
        } else {
            EdumateNavHost(
                navController = navController,
                drawerState = drawerState
            )
        }
    }
}