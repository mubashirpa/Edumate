package app.edumate.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import app.edumate.core.Navigation
import app.edumate.presentation.createCourse.CreateCourseScreen
import app.edumate.presentation.home.HomeScreen
import app.edumate.presentation.profile.ProfileScreen

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
        composable<Screen.Home> {
            HomeScreen(
                navController = navController,
                onNavigateToCreateCourse = { courseId ->
                    navController.navigate(Screen.CreateCourse(courseId))
                },
                onNavigateToCourseDetails = { /*TODO*/ },
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
                        popUpTo(Screen.Home) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
            )
        }
        composable<Screen.CreateCourse> { backStackEntry ->
            val courseId = backStackEntry.toRoute<Screen.CreateCourse>().courseId
            CreateCourseScreen(
                onNavigateToCourseDetails = { id ->
                    navController.previousBackStackEntry?.savedStateHandle[Navigation.Args.HOME_NEW_TEACHING_COURSE_ID] =
                        id
                    // TODO: Navigate to course details screen
                    navController.navigateUp()
                },
                onNavigateUp = navController::navigateUp,
                courseId = courseId,
            )
        }
    }
}
