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
import edumate.app.presentation.class_details.ClassDetailsUiEvent
import edumate.app.presentation.class_details.ClassDetailsUiState
import edumate.app.presentation.class_details.UserType
import edumate.app.presentation.classwork.ClassworkViewModel
import edumate.app.presentation.classwork.screen.ClassworkScreen
import edumate.app.presentation.create_classwork.CreateClassworkViewModel
import edumate.app.presentation.create_classwork.screen.CreateClassworkScreen
import edumate.app.presentation.people.screen.PeopleScreen
import edumate.app.presentation.stream.screen.StreamScreen
import edumate.app.presentation.view_classwork.ViewClassworkViewModel
import edumate.app.presentation.view_classwork.screen.ViewClassworkScreen
import edumate.app.presentation.view_student_work.ViewStudentWorkViewModel
import edumate.app.presentation.view_student_work.screen.ViewStudentWorkScreen

@Composable
fun ClassDetailsNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    uiState: ClassDetailsUiState,
    onEvent: (ClassDetailsUiEvent) -> Unit,
    onLeaveClass: () -> Unit,
    onBackPressed: () -> Unit
) {
    val course = uiState.course!!
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
                            null,
                            workType.toString()
                        )
                    )
                },
                navigateToEditClasswork = { courseId, classworkId, workType ->
                    navController.navigate(
                        Screen.CreateClassworkScreen.withArgs(
                            courseId,
                            classworkId,
                            workType.toString()
                        )
                    )
                },
                navigateToViewClasswork = { courseId, classworkId, workType, userType ->
                    navController.navigate(
                        Screen.ViewClassworkScreen.withArgs(
                            courseId,
                            classworkId,
                            workType.toString(),
                            userType.toString()
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
                    defaultValue = course.id
                },
                navArgument(Routes.Args.CREATE_CLASSWORK_ID) { type = NavType.StringType },
                navArgument(Routes.Args.CREATE_CLASSWORK_TYPE) { type = NavType.StringType }
            )
        ) {
            val viewModel: CreateClassworkViewModel = hiltViewModel()
            CreateClassworkScreen(
                uiState = viewModel.uiState,
                onEvent = viewModel::onEvent,
                createClassworkResults = viewModel.createClassworkResults,
                snackbarHostState = snackbarHostState,
                className = course.name,
                onCreateClassworkSuccess = { navController.navigateUp() },
                onBackPressed = { navController.navigateUp() }
            )
        }
        composable(
            route = "${Screen.ViewClassworkScreen.route}${Routes.Args.VIEW_CLASSWORK_SCREEN}",
            arguments = listOf(
                navArgument(Routes.Args.VIEW_CLASSWORK_COURSE_ID) {
                    type = NavType.StringType
                    defaultValue = course.id
                },
                navArgument(Routes.Args.VIEW_CLASSWORK_ID) { type = NavType.StringType },
                navArgument(Routes.Args.VIEW_CLASSWORK_TYPE) { type = NavType.StringType },
                navArgument(Routes.Args.VIEW_CLASSWORK_USER_TYPE) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val viewModel: ViewClassworkViewModel = hiltViewModel()
            val classworkType: CourseWorkType =
                backStackEntry.arguments?.getString(Routes.Args.VIEW_CLASSWORK_TYPE).orEmpty()
                    .enumValueOf(CourseWorkType.COURSE_WORK_TYPE_UNSPECIFIED)!!
            val currentUserType: UserType =
                backStackEntry.arguments?.getString(Routes.Args.VIEW_CLASSWORK_USER_TYPE).orEmpty()
                    .enumValueOf(UserType.STUDENT)!!

            ViewClassworkScreen(
                uiState = viewModel.uiState,
                onEvent = viewModel::onEvent,
                snackbarHostState = snackbarHostState,
                classworkType = classworkType,
                currentUserType = currentUserType,
                navigateToViewStudentWork = { courseWork, studentWorkId, assignedStudent ->
                    onEvent(
                        ClassDetailsUiEvent.OnNavigateToViewStudentWork(
                            courseWork,
                            assignedStudent
                        )
                    )
                    navController.navigate(
                        Screen.ViewStudentWorkScreen.withArgs(
                            courseWork.courseId,
                            courseWork.id,
                            studentWorkId
                        )
                    )
                },
                onBackPressed = { navController.navigateUp() }
            )
        }
        composable(
            route = "${Screen.ViewStudentWorkScreen.route}${Routes.Args.VIEW_STUDENT_WORK_SCREEN}",
            arguments = listOf(
                navArgument(Routes.Args.VIEW_STUDENT_WORK_COURSE_ID) {
                    type = NavType.StringType
                    defaultValue = course.id
                },
                navArgument(Routes.Args.VIEW_STUDENT_WORK_COURSE_WORK_ID) {
                    type = NavType.StringType
                },
                navArgument(Routes.Args.VIEW_STUDENT_WORK_ID) { type = NavType.StringType }
            )
        ) {
            val viewModel: ViewStudentWorkViewModel = hiltViewModel()
            ViewStudentWorkScreen(
                uiState = viewModel.uiState,
                onEvent = viewModel::onEvent,
                snackbarHostState = snackbarHostState,
                courseWork = uiState.courseWork!!,
                assignedStudent = uiState.courseWorkAssignedStudent!!,
                onBackPressed = { navController.navigateUp() }
            )
        }
    }
}