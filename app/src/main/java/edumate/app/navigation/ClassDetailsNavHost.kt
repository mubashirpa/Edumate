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
import edumate.app.presentation.classDetails.ClassDetailsUiEvent
import edumate.app.presentation.classDetails.ClassDetailsUiState
import edumate.app.presentation.createAnnouncement.CreateAnnouncementScreen
import edumate.app.presentation.createAnnouncement.CreateAnnouncementViewModel
import edumate.app.presentation.createClasswork.CreateClassworkScreen
import edumate.app.presentation.createClasswork.CreateClassworkViewModel
import edumate.app.presentation.people.PeopleScreen

@Composable
fun ClassDetailsNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    uiState: ClassDetailsUiState,
    onEvent: (ClassDetailsUiEvent) -> Unit,
    onLeaveClass: () -> Unit,
    onBackPressed: () -> Unit,
) {
    val course = uiState.course!!
    NavHost(
        navController = navController,
        startDestination = Screen.StreamScreen.route,
        modifier = modifier,
    ) {
        composable(
            route = Screen.StreamScreen.route,
            arguments =
                listOf(
                    navArgument(Routes.Args.STREAM_SCREEN_COURSE_ID) {
                        type = NavType.StringType
                        defaultValue = course.id
                    },
                ),
        ) {
        }
        composable(
            route = Screen.ClassworkScreen.route,
            arguments =
                listOf(
                    navArgument(Routes.Args.CLASSWORK_COURSE_ID) {
                        type = NavType.StringType
                        defaultValue = course.id
                    },
                ),
        ) {
        }
        composable(
            route = Screen.PeopleScreen.route,
            arguments =
                listOf(
                    navArgument(Routes.Args.PEOPLE_COURSE_ID) {
                        type = NavType.StringType
                        defaultValue = course.id
                    },
                    navArgument(Routes.Args.PEOPLE_COURSE_OWNER_ID) {
                        type = NavType.StringType
                        defaultValue = course.ownerId
                    },
                ),
        ) {

        }
        composable(
            route = "${Screen.CreateClassworkScreen.route}${Routes.Args.CREATE_CLASSWORK_SCREEN}",
            arguments =
                listOf(
                    navArgument(Routes.Args.CREATE_CLASSWORK_COURSE_ID) {
                        type = NavType.StringType
                        defaultValue = course.id
                    },
                    navArgument(Routes.Args.CREATE_CLASSWORK_ID) { type = NavType.StringType },
                    navArgument(Routes.Args.CREATE_CLASSWORK_TYPE) { type = NavType.StringType },
                ),
        ) {
            val viewModel: CreateClassworkViewModel = hiltViewModel()
            CreateClassworkScreen(
                uiState = viewModel.uiState,
                onEvent = viewModel::onEvent,
                createClassworkResults = viewModel.createClassworkResults,
                snackbarHostState = snackbarHostState,
                className = course.name.orEmpty(),
                onCreateClassworkSuccess = { navController.navigateUp() },
                onBackPressed = { navController.navigateUp() },
            )
        }
        composable(
            route = "${Screen.ViewClassworkScreen.route}${Routes.Args.VIEW_CLASSWORK_SCREEN}",
            arguments =
                listOf(
                    navArgument(Routes.Args.VIEW_CLASSWORK_COURSE_ID) {
                        type = NavType.StringType
                        defaultValue = course.id
                    },
                    navArgument(Routes.Args.VIEW_CLASSWORK_ID) { type = NavType.StringType },
                    navArgument(Routes.Args.VIEW_CLASSWORK_TYPE) { type = NavType.StringType },
                    navArgument(Routes.Args.VIEW_CLASSWORK_USER_TYPE) { type = NavType.StringType },
                ),
        ) { backStackEntry ->
        }
        composable(
            route = "${Screen.ViewStudentWorkScreen.route}${Routes.Args.VIEW_STUDENT_WORK_SCREEN}",
            arguments =
                listOf(
                    navArgument(Routes.Args.VIEW_STUDENT_WORK_COURSE_ID) {
                        type = NavType.StringType
                        defaultValue = course.id
                    },
                    navArgument(Routes.Args.VIEW_STUDENT_WORK_COURSE_WORK_ID) {
                        type = NavType.StringType
                    },
                    navArgument(Routes.Args.VIEW_STUDENT_WORK_ID) { type = NavType.StringType },
                ),
        ) {
        }
        composable(
            route = "${Screen.CreateAnnouncementScreen.route}${Routes.Args.CREATE_ANNOUNCEMENT_SCREEN}",
            arguments =
                listOf(
                    navArgument(Routes.Args.CREATE_ANNOUNCEMENT_COURSE_ID) {
                        type = NavType.StringType
                        defaultValue = course.id
                    },
                    navArgument(Routes.Args.CREATE_ANNOUNCEMENT_ID) { type = NavType.StringType },
                ),
        ) {
            val viewModel: CreateAnnouncementViewModel = hiltViewModel()
            CreateAnnouncementScreen(
                uiState = viewModel.uiState,
                onEvent = viewModel::onEvent,
                snackbarHostState = snackbarHostState,
                createAnnouncementResults = viewModel.createAnnouncementResults,
                className = course.name.orEmpty(),
                onCreateAnnouncementSuccess = { navController.navigateUp() },
                onBackPressed = { navController.navigateUp() },
            )
        }
        composable(
            route = Screen.MeetScreen.route,
            arguments =
                listOf(
                    navArgument(Routes.Args.MEET_SCREEN_COURSE_ID) {
                        type = NavType.StringType
                        defaultValue = course.id
                    },
                ),
        ) {
        }
    }
}
