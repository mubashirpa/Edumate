package edumate.app.presentation

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import edumate.app.navigation.EdumateNavHost
import edumate.app.navigation.Routes
import edumate.app.navigation.Screen
import edumate.app.presentation.components.EdumateDrawerContent
import edumate.app.presentation.components.EdumateSnackbarHost
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EdumateApp(isLoggedIn: Boolean) {
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            EdumateSnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.navigationBarsPadding(),
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { innerPadding ->
        MPSNavigationWrapper(
            snackbarHostState = snackbarHostState,
            snackbarScope = snackbarScope,
            modifier =
                Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal),
                    ),
            isLoggedIn = isLoggedIn,
        )
    }
}

@Composable
private fun MPSNavigationWrapper(
    snackbarHostState: SnackbarHostState,
    snackbarScope: CoroutineScope,
    modifier: Modifier = Modifier,
    isLoggedIn: Boolean,
) {
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val rootNavController = rememberNavController()
    val startDestination =
        if (isLoggedIn) Routes.Screen.HOME_SCREEN else Routes.Graph.AUTHENTICATION
    val navBackStackEntry by rootNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val gesturesEnabled = currentDestination?.route == Screen.HomeScreen.route

    BackHandler(drawerState.isOpen) {
        coroutineScope.launch {
            drawerState.close()
        }
    }

    ModalNavigationDrawer(
        drawerContent = {
            EdumateDrawerContent(
                onItemClick = { index ->
                    coroutineScope.launch {
                        drawerState.close()
                    }
                    when (index) {
                        0 -> {
                            rootNavController.navigate(Screen.ProfileScreen.route)
                        }

                        1 -> {
                            rootNavController.navigate(Screen.SettingsScreen.route)
                        }
                    }
                },
            )
        },
        drawerState = drawerState,
        gesturesEnabled = gesturesEnabled,
    ) {
        Box(modifier = modifier) {
            EdumateAppContent(
                navController = rootNavController,
                startDestination = startDestination,
                drawerState = drawerState,
                snackbarHostState = snackbarHostState,
                snackbarScope = snackbarScope,
            )
        }
    }
}

@Composable
private fun EdumateAppContent(
    navController: NavHostController,
    startDestination: String = Routes.Graph.AUTHENTICATION,
    drawerState: DrawerState,
    snackbarHostState: SnackbarHostState,
    snackbarScope: CoroutineScope,
) {
    EdumateNavHost(
        navController = navController,
        modifier = Modifier.fillMaxSize(),
        startDestination = startDestination,
        drawerState = drawerState,
        snackbarHostState = snackbarHostState,
        snackbarScope = snackbarScope,
    )
}
