package edumate.app.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import edumate.app.domain.model.courses.Course
import edumate.app.presentation.classwork.ClassworkViewModel
import edumate.app.presentation.classwork.screen.ClassworkScreen
import edumate.app.presentation.create_classwork.CreateClassworkViewModel
import edumate.app.presentation.create_classwork.screen.CreateClassworkScreen
import edumate.app.presentation.people.screen.PeopleScreen
import edumate.app.presentation.stream.screen.StreamScreen
import edumate.app.presentation.view_classwork.screen.ViewClassworkScreen

@Composable
fun ClassDetailsNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    course: Course,
    onLeaveClass: () -> Unit,
    onBackPressed: () -> Unit
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
            val viewModel: ClassworkViewModel = hiltViewModel()
            ClassworkScreen(
                uiState = viewModel.uiState,
                onEvent = viewModel::onEvent,
                snackbarHostState = snackbarHostState,
                course = course,
                navigateToCreateClasswork = { courseId, courseName, workType ->
                    navController.navigate(
                        Screen.CreateClassworkScreen.withArgs(
                            courseId,
                            courseName,
                            workType.toString()
                        )
                    )
                },
                navigateToViewClasswork = {
                    navController.navigate("view")
                },
                onBackPressed = onBackPressed
            )
        }
        composable(
            route = Screen.PeopleScreen.route,
            arguments = arguments
        ) {
            PeopleScreen(
                snackbarHostState = snackbarHostState,
                course = course,
                onLeaveClass = onLeaveClass,
                onBackPressed = onBackPressed
            )
        }
        composable(
            route = "${Screen.CreateClassworkScreen.route}${Routes.Args.CREATE_CLASSWORK_SCREEN}",
            arguments = listOf(
                navArgument(Routes.Args.CREATE_CLASSWORK_COURSE_ID) {
                    type = NavType.StringType
                },
                navArgument(Routes.Args.CREATE_CLASSWORK_COURSE_NAME) {
                    type = NavType.StringType
                },
                navArgument(Routes.Args.CREATE_CLASSWORK_TYPE) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val viewModel: CreateClassworkViewModel = hiltViewModel()
            val courseName =
                backStackEntry.arguments?.getString(Routes.Args.CREATE_CLASSWORK_COURSE_NAME)
                    .orEmpty()
            CreateClassworkScreen(
                uiState = viewModel.uiState,
                onEvent = viewModel::onEvent,
                createClassworkResults = viewModel.createClassworkResults,
                snackbarHostState = snackbarHostState,
                className = courseName,
                onCreateClassworkSuccess = {
                    navController.navigateUp()
                },
                onBackPressed = {
                    navController.navigateUp()
                }
            )
        }
        composable(route = "view") {
            ViewClassworkScreen(
                onBackPressed = {
                    navController.navigateUp()
                }
            )
        }
    }
}