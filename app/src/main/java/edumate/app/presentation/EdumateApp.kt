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
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EdumateApp(isLoggedIn: Boolean) {
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
        MPSNavigationWrapper(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)
                ),
            snackbarHostState = snackbarHostState,
            isLoggedIn = isLoggedIn
        )
    }
}

@Composable
private fun MPSNavigationWrapper(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    isLoggedIn: Boolean
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
                onProfileClicked = {
                    coroutineScope.launch {
                        drawerState.close()
                    }
                    rootNavController.navigate(Screen.ProfileScreen.route)
                }
            )
        },
        drawerState = drawerState,
        gesturesEnabled = gesturesEnabled
    ) {
        Box(modifier = modifier) {
            EdumateAppContent(
                navController = rootNavController,
                startDestination = startDestination,
                drawerState = drawerState,
                snackbarHostState = snackbarHostState
            )
        }
    }
}

@Composable
private fun EdumateAppContent(
    navController: NavHostController,
    startDestination: String = Routes.Graph.AUTHENTICATION,
    drawerState: DrawerState,
    snackbarHostState: SnackbarHostState
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        EdumateNavHost(
            navController = navController,
            modifier = Modifier.fillMaxSize(),
            startDestination = startDestination,
            drawerState = drawerState,
            snackbarHostState = snackbarHostState
        )
    }
}