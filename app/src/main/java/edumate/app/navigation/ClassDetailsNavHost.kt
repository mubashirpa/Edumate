package edumate.app.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import edumate.app.core.utils.enumValueOf
import edumate.app.domain.model.course_work.CourseWorkType
import edumate.app.domain.model.courses.Course
import edumate.app.presentation.class_details.UserType
import edumate.app.presentation.classwork.ClassworkViewModel
import edumate.app.presentation.classwork.screen.ClassworkScreen
import edumate.app.presentation.create_classwork.CreateClassworkViewModel
import edumate.app.presentation.create_classwork.screen.CreateClassworkScreen
import edumate.app.presentation.people.screen.PeopleScreen
import edumate.app.presentation.stream.screen.StreamScreen
import edumate.app.presentation.view_classwork.ViewClassworkViewModel
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
        composable(route = Screen.StreamScreen.route) {
            StreamScreen()
        }
        composable(
            route = Screen.ClassworkScreen.route,
            arguments = listOf(
                navArgument(Routes.Args.CLASSWORK_COURSE_ID) {
                    type = NavType.StringType
                    defaultValue = course.id
                }
            )
        ) {
            val viewModel: ClassworkViewModel = hiltViewModel()
            ClassworkScreen(
                uiState = viewModel.uiState,
                onEvent = viewModel::onEvent,
                snackbarHostState = snackbarHostState,
                course = course,
                navigateToCreateClasswork = { courseId, workType ->
                    navController.navigate(
                        Screen.CreateClassworkScreen.withArgs(
                            courseId,
                            workType.toString()
                        )
                    )
                },
                navigateToEditClasswork = { _, _, _ ->
                    // TODO("Not yet implemented")
                },
                navigateToViewClasswork = { courseId, classworkId, workType, userType ->
                    navController.navigate(
                        Screen.ViewClassworkScreen.withArgs(
                            classworkId,
                            workType.toString(),
                            userType.toString(),
                            courseId
                        )
                    )
                },
                onBackPressed = onBackPressed
            )
        }
        composable(
            route = Screen.PeopleScreen.route,
            arguments = listOf(
                navArgument(Routes.Args.PEOPLE_COURSE_ID) {
                    type = NavType.StringType
                    defaultValue = course.id
                },
                navArgument(Routes.Args.PEOPLE_COURSE_OWNER_ID) {
                    type = NavType.StringType
                    defaultValue = course.ownerId
                }
            )
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
                navArgument(Routes.Args.CREATE_CLASSWORK_TYPE) {
                    type = NavType.StringType
                }
            )
        ) {
            val viewModel: CreateClassworkViewModel = hiltViewModel()
            CreateClassworkScreen(
                uiState = viewModel.uiState,
                onEvent = viewModel::onEvent,
                createClassworkResults = viewModel.createClassworkResults,
                snackbarHostState = snackbarHostState,
                className = course.name,
                onCreateClassworkSuccess = {
                    navController.navigateUp()
                },
                onBackPressed = {
                    navController.navigateUp()
                }
            )
        }
        composable(
            route = "${Screen.ViewClassworkScreen.route}${Routes.Args.VIEW_CLASSWORK_SCREEN}",
            arguments = listOf(
                navArgument(Routes.Args.VIEW_CLASSWORK_WORK_ID) {
                    type = NavType.StringType
                },
                navArgument(Routes.Args.VIEW_CLASSWORK_WORK_TYPE) {
                    type = NavType.StringType
                },
                navArgument(Routes.Args.VIEW_CLASSWORK_COURSE_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val viewModel: ViewClassworkViewModel = hiltViewModel()
            val classworkType: CourseWorkType? =
                backStackEntry.arguments?.getString(Routes.Args.VIEW_CLASSWORK_WORK_TYPE).orEmpty()
                    .enumValueOf(CourseWorkType.COURSE_WORK_TYPE_UNSPECIFIED)
            val currentUserType: UserType? =
                backStackEntry.arguments?.getString(Routes.Args.VIEW_CLASSWORK_USER_TYPE).orEmpty()
                    .enumValueOf(UserType.UNKNOWN)

            ViewClassworkScreen(
                uiState = viewModel.uiState,
                classworkType = classworkType!!,
                currentUserType = currentUserType!!,
                onBackPressed = {
                    navController.navigateUp()
                }
            )
        }
    }
}