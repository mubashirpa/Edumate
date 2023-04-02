package edumate.app.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import edumate.app.navigation.EdumateNavHost
import edumate.app.navigation.Routes
import edumate.app.presentation.components.EdumateDrawerContent
import edumate.app.presentation.components.EdumateSnackbarHost
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EdumateApp(
    isLoggedIn: Boolean
) {
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            EdumateSnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.navigationBarsPadding()
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        ModalNavigationDrawer(
            drawerContent = {
                EdumateDrawerContent(
                    onProfileClicked = {
                        coroutineScope.launch {
                            drawerState.close()
                            snackbarHostState.showSnackbar("Hello")
                        }
                    }
                )
            },
            drawerState = drawerState
        ) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)
                    )
            ) {
                EdumateAppContent(
                    isLoggedIn = isLoggedIn,
                    drawerState = drawerState,
                    snackbarHostState = snackbarHostState
                )
            }
        }
    }
}

@Composable
private fun EdumateAppContent(
    isLoggedIn: Boolean,
    drawerState: DrawerState,
    snackbarHostState: SnackbarHostState
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val navController = rememberNavController()
        val startDestination =
            if (isLoggedIn) Routes.Screen.HOME_SCREEN else Routes.Graph.AUTHENTICATION

        EdumateNavHost(
            navController = navController,
            modifier = Modifier.fillMaxSize(),
            startDestination = startDestination,
            drawerState = drawerState,
            snackbarHostState = snackbarHostState
        )
    }
}