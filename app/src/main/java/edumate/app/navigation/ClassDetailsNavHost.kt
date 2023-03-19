package edumate.app.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import edumate.app.domain.model.courses.Course
import edumate.app.presentation.classwork.screen.ClassworkScreen
import edumate.app.presentation.create_classwork.screen.CreateClassworkScreen
import edumate.app.presentation.people.screen.PeopleScreen
import edumate.app.presentation.stream.screen.StreamScreen

@Composable
fun ClassDetailsNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    course: Course,
    onLeaveClass: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.StreamScreen.route,
        modifier = modifier
    ) {
        val arguments: List<NamedNavArgument> = listOf(
            navArgument(Routes.Args.CLASS_DETAILS_COURSE_ID) {
                type = NavType.StringType
                defaultValue = course.id.orEmpty()
            }
        )

        composable(
            route = Screen.StreamScreen.route,
            arguments = arguments
        ) {
            StreamScreen()
        }
        composable(
            route = Screen.ClassworkScreen.route,
            arguments = arguments
        ) {
            ClassworkScreen(
                snackbarHostState = snackbarHostState,
                course = course,
                navigateToCreateClasswork = { workType ->
                    navController.navigate(
                        Screen.CreateClassworkScreen.withArgs(workType.toString())
                    )
                }
            )
        }
        composable(
            route = Screen.PeopleScreen.route,
            arguments = arguments
        ) {
            PeopleScreen(
                snackbarHostState = snackbarHostState,
                course = course,
                onLeaveClass = onLeaveClass
            )
        }
        composable(
            route = "${Screen.CreateClassworkScreen.route}${Routes.Args.CREATE_CLASSWORK_SCREEN}",
            arguments = listOf(
                navArgument(Routes.Args.CREATE_CLASSWORK_TYPE) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val workType =
                backStackEntry.arguments?.getString(Routes.Args.CREATE_CLASSWORK_TYPE).orEmpty()
            CreateClassworkScreen(workType = workType)
        }
    }
}