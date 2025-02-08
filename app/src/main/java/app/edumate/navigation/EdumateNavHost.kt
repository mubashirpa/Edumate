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
import app.edumate.core.ext.GetOnceResult
import app.edumate.presentation.courseDetails.CourseDetailsScreen
import app.edumate.presentation.courseDetails.CourseDetailsUiEvent
import app.edumate.presentation.courseDetails.CourseDetailsViewModel
import app.edumate.presentation.createCourse.CreateCourseScreen
import app.edumate.presentation.home.HomeScreen
import app.edumate.presentation.home.HomeUiEvent
import app.edumate.presentation.home.HomeViewModel
import app.edumate.presentation.imageViewer.ImageViewerScreen
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
    val isNotAuthenticated = startDestination == Graph.Authentication

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
                if (isNotAuthenticated) {
                    emptyList()
                } else {
                    listOf(
                        navDeepLink<Screen.Home>(
                            basePath = "${Constants.EDUMATE_BASE_URL}course",
                        ),
                    )
                },
        ) {
            val viewModel: HomeViewModel = koinViewModel()

            navController.GetOnceResult<Boolean>(Navigation.Args.CREATE_COURSE_SUCCESS) { refresh ->
                if (refresh) {
                    viewModel.onEvent(HomeUiEvent.Refresh)
                }
            }

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
                viewModel = viewModel,
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
                        ?.savedStateHandle[Navigation.Args.CREATE_COURSE_SUCCESS] = true
                    if (route.courseId == null) {
                        navController.navigate(Screen.CourseDetails(courseId)) {
                            popUpTo(Screen.Home.ROUTE)
                        }
                    } else {
                        // For course details screen
                        navController.previousBackStackEntry
                            ?.savedStateHandle[Navigation.Args.UPDATE_COURSE_SETTINGS_SUCCESS] = true
                        navController.navigateUp()
                    }
                },
                onNavigateUp = navController::navigateUp,
                courseId = route.courseId,
            )
        }
        composable<Screen.CourseDetails>(
            deepLinks =
                if (isNotAuthenticated) {
                    emptyList()
                } else {
                    listOf(
                        navDeepLink<Screen.CourseDetails>(
                            basePath = "${Constants.EDUMATE_BASE_URL}course",
                        ),
                    )
                },
        ) {
            val viewModel: CourseDetailsViewModel = koinViewModel()

            navController.GetOnceResult<Boolean>(Navigation.Args.UPDATE_COURSE_SETTINGS_SUCCESS) { refresh ->
                if (refresh) {
                    viewModel.onEvent(CourseDetailsUiEvent.Retry)
                }
            }

            CourseDetailsScreen(
                uiState = viewModel.uiState,
                onEvent = viewModel::onEvent,
                onNavigateUp = navController::navigateUp,
                onNavigateToCourseSettings = { courseId ->
                    navController.navigate(Screen.CreateCourse(courseId))
                },
                onNavigateToImageViewer = { url, title ->
                    navController.navigate(Screen.ImageViewer(url, title))
                },
                onLeaveCourse = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle[Navigation.Args.CREATE_COURSE_SUCCESS] = true
                    navController.navigateUp()
                },
            )
        }
        composable<Screen.ImageViewer> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.ImageViewer>()
            ImageViewerScreen(
                imageUrl = route.imageUrl,
                onNavigateUp = navController::navigateUp,
                title = route.title,
            )
        }
    }
}
