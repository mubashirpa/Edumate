package edumate.app.navigation

import androidx.compose.material3.DrawerState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import edumate.app.presentation.class_details.ClassDetailsViewModel
import edumate.app.presentation.class_details.screen.ClassDetailsScreen
import edumate.app.presentation.create_class.CreateClassViewModel
import edumate.app.presentation.create_class.screen.CreateClassScreen
import edumate.app.presentation.home.HomeViewModel
import edumate.app.presentation.home.screen.HomeScreen
import edumate.app.presentation.join_class.JoinClassViewModel
import edumate.app.presentation.join_class.screen.JoinClassScreen
import edumate.app.presentation.profile.ProfileViewModel
import edumate.app.presentation.profile.screen.ProfileScreen
import edumate.app.presentation.settings.SettingsViewModel
import edumate.app.presentation.settings.screen.SettingsScreen
import kotlinx.coroutines.CoroutineScope

@Composable
fun EdumateNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Routes.Graph.AUTHENTICATION,
    drawerState: DrawerState,
    snackbarHostState: SnackbarHostState,
    snackbarScope: CoroutineScope
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        authentication(navController, snackbarHostState, snackbarScope)
        composable(route = Screen.HomeScreen.route) {
            val viewModel: HomeViewModel = hiltViewModel()
            HomeScreen(
                uiState = viewModel.uiState,
                onEvent = viewModel::onEvent,
                drawerState = drawerState,
                snackbarHostState = snackbarHostState,
                navigateToClassDetails = { courseId ->
                    navController.navigate(Screen.ClassDetailsScreen.withArgs(courseId))
                },
                navigateToCreateClass = { courseId ->
                    val createClassRoute = if (courseId != null) {
                        "${Screen.CreateClassScreen.route}?${Routes.Args.CREATE_CLASS_COURSE_ID}=$courseId"
                    } else {
                        Screen.CreateClassScreen.route
                    }
                    navController.navigate(createClassRoute)
                },
                navigateToJoinClass = {
                    navController.navigate(Screen.JoinClassScreen.route)
                },
                navigateToProfile = {
                    navController.navigate(Screen.ProfileScreen.route)
                }
            )
        }
        composable(
            route = "${Screen.CreateClassScreen.route}${Routes.Args.CREATE_CLASS_SCREEN}",
            arguments = listOf(
                navArgument(Routes.Args.CREATE_CLASS_COURSE_ID) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val viewModel: CreateClassViewModel = hiltViewModel()
            val courseId = backStackEntry.arguments?.getString(Routes.Args.CREATE_CLASS_COURSE_ID)

            CreateClassScreen(
                uiState = viewModel.uiState,
                onEvent = viewModel::onEvent,
                createClassResults = viewModel.createClassResults,
                courseId = courseId,
                navigateToClassDetails = {
                    navController.navigate(Screen.ClassDetailsScreen.withArgs(it)) {
                        popUpTo(Screen.HomeScreen.route)
                        launchSingleTop = true
                    }
                },
                onBackPressed = {
                    navController.navigateUp()
                }
            )
        }
        composable(route = Screen.JoinClassScreen.route) {
            val viewModel: JoinClassViewModel = hiltViewModel()
            JoinClassScreen(
                uiState = viewModel.uiState,
                onEvent = viewModel::onEvent,
                joinClassResults = viewModel.joinClassResults,
                navigateToClassDetails = { courseId ->
                    navController.navigate(Screen.ClassDetailsScreen.withArgs(courseId)) {
                        popUpTo(Screen.HomeScreen.route)
                        launchSingleTop = true
                    }
                },
                navigateToProfile = {
                    navController.navigate(Screen.ProfileScreen.route)
                },
                onBackPressed = {
                    navController.navigateUp()
                }
            )
        }
        composable(
            route = "${Screen.ClassDetailsScreen.route}${Routes.Args.CLASS_DETAILS_SCREEN}",
            arguments = listOf(
                navArgument(Routes.Args.CLASS_DETAILS_COURSE_ID) {
                    type = NavType.StringType
                    defaultValue = Routes.Args.CLASS_DETAILS_DEFAULT_COURSE_ID
                }
            )
        ) {
            val viewModel: ClassDetailsViewModel = hiltViewModel()
            ClassDetailsScreen(
                uiState = viewModel.uiState,
                onEvent = viewModel::onEvent,
                onLeaveClass = {
                    navController.navigateUp()
                },
                onBackPressed = {
                    navController.navigateUp()
                }
            )
        }
        composable(route = Screen.ProfileScreen.route) {
            val viewModel: ProfileViewModel = hiltViewModel()
            ProfileScreen(
                uiState = viewModel.uiState,
                onEvent = viewModel::onEvent,
                onSignOut = {
                    navController.navigate(Routes.Graph.AUTHENTICATION) {
                        launchSingleTop = true
                        popUpTo(Screen.HomeScreen.route) { inclusive = true }
                    }
                },
                onBackPressed = {
                    navController.navigateUp()
                }
            )
        }
        composable(route = Screen.SettingsScreen.route) {
            val viewModel: SettingsViewModel = hiltViewModel()
            SettingsScreen(
                uiState = viewModel.uiState,
                onEvent = viewModel::onEvent,
                onBackPressed = {
                    navController.navigateUp()
                }
            )
        }
    }
}