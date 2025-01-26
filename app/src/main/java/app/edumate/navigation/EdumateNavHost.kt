package app.edumate.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import app.edumate.core.Constants
import app.edumate.core.Navigation
import app.edumate.presentation.courseDetails.CourseDetailsScreen
import app.edumate.presentation.courseDetails.CourseDetailsViewModel
import app.edumate.presentation.createCourse.CreateCourseScreen
import app.edumate.presentation.home.HomeScreen
import app.edumate.presentation.profile.ProfileScreen
import org.koin.androidx.compose.koinViewModel

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
        composable<Screen.Home>(
            deepLinks =
                listOf(
                    navDeepLink<Screen.Home>(basePath = "${Constants.EDUMATE_BASE_URL}course"),
                ),
        ) {
            HomeScreen(
                navController = navController,
                onNavigateToCreateCourse = { courseId ->
                    navController.navigate(Screen.CreateCourse(courseId))
                },
                onNavigateToCourseDetails = { courseId ->
                    navController.navigate(Screen.CourseDetails(courseId))
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile)
                },
            )
        }
        composable<Screen.Profile> {
            ProfileScreen(
                onNavigateUp = navController::navigateUp,
                onSignOutComplete = {
                    navController.navigate(Graph.Authentication) {
                        popUpTo(Screen.Home.ROUTE) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
            )
        }
        composable<Screen.CreateCourse> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.CreateCourse>()
            CreateCourseScreen(
                onNavigateToCourseDetails = { courseId ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle[Navigation.Args.HOME_NEW_TEACHING_COURSE_ID] = courseId
                    navController.navigate(Screen.CourseDetails(courseId)) {
                        popUpTo(route) { inclusive = true }
                    }
                },
                onNavigateUp = navController::navigateUp,
                courseId = route.courseId,
            )
        }
        composable<Screen.CourseDetails>(
            deepLinks =
                listOf(
                    navDeepLink<Screen.CourseDetails>(basePath = "${Constants.EDUMATE_BASE_URL}course"),
                ),
        ) {
            val viewModel: CourseDetailsViewModel = koinViewModel()
            CourseDetailsScreen(
                uiState = viewModel.uiState,
                onEvent = viewModel::onEvent,
                onNavigateUp = navController::navigateUp,
            )
        }
    }
}
