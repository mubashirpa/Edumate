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
import edumate.app.domain.model.classroom.courses.Course
import edumate.app.presentation.classwork.ClassworkScreen
import edumate.app.presentation.classwork.ClassworkViewModel
import edumate.app.presentation.createAnnouncement.CreateAnnouncementScreen
import edumate.app.presentation.createClasswork.CreateClassworkScreen
import edumate.app.presentation.people.PeopleScreen
import edumate.app.presentation.stream.StreamScreen
import edumate.app.presentation.stream.StreamViewModel

@Composable
fun ClassDetailsNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    course: Course,
    onLeaveClass: () -> Unit,
    onBackPressed: () -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.StreamScreen.route,
        modifier = modifier,
    ) {
        // For the first three screens, arguments are passed through default values because we can't
        // pass the course ID argument from the navigation bar as it navigates using a navigation bar.
        composable(
            route = Screen.StreamScreen.route,
            arguments =
                listOf(
                    navArgument(Routes.Args.STREAM_SCREEN_COURSE_ID) {
                        defaultValue = course.id
                        type = NavType.StringType
                    },
                ),
        ) {
            val viewModel: StreamViewModel = hiltViewModel()

            StreamScreen(
                uiState = viewModel.uiState,
                onEvent = viewModel::onEvent,
                snackbarHostState = snackbarHostState,
                course = course,
                navigateToCreateAnnouncement = { courseId ->
                    navController.navigate(Screen.CreateAnnouncementScreen.withArgs(courseId))
                },
                navigateToEditAnnouncement = { courseId, id ->
                    navController.navigate(
                        Screen.CreateAnnouncementScreen.withArgs(courseId)
                            .plus(if (id != null) "?${Routes.Args.CREATE_ANNOUNCEMENT_ID}=$id" else ""),
                    )
                },
                navigateToViewAnnouncement = { _, _ ->
                    // TODO
                },
                onBackPressed = onBackPressed,
            )
        }
        composable(
            route = Screen.ClassworkScreen.route,
            arguments =
                listOf(
                    navArgument(Routes.Args.CLASSWORK_COURSE_ID) {
                        defaultValue = course.id
                        type = NavType.StringType
                    },
                ),
        ) {
            val viewModel: ClassworkViewModel = hiltViewModel()

            ClassworkScreen(
                uiState = viewModel.uiState,
                onEvent = viewModel::onEvent,
                snackbarHostState = snackbarHostState,
                course = course,
                navigateToCreateClasswork = { courseId, workType, id ->
                    navController.navigate(
                        Screen.CreateClassworkScreen.withArgs(
                            courseId,
                            workType.name,
                        ).plus(if (id != null) "?${Routes.Args.CREATE_CLASSWORK_ID}=$id" else ""),
                    )
                },
                navigateToCreateMaterial = { _, _ ->
                    // TODO
                },
                navigateToViewClasswork = { _, _, _ ->
                    // TODO
                },
                onBackPressed = onBackPressed,
            )
        }
        composable(
            route = Screen.PeopleScreen.route,
            arguments =
                listOf(
                    navArgument(Routes.Args.PEOPLE_COURSE_ID) {
                        defaultValue = course.id
                        type = NavType.StringType
                    },
                ),
        ) {
            PeopleScreen(
                snackbarHostState = snackbarHostState,
                course = course,
                onLeaveClass = onLeaveClass,
                onBackPressed = onBackPressed,
            )
        }
        composable(
            route = "${Screen.CreateClassworkScreen.route}${Routes.Args.CREATE_CLASSWORK_SCREEN}",
            arguments =
                listOf(
                    navArgument(Routes.Args.CREATE_CLASSWORK_COURSE_ID) {
                        type = NavType.StringType
                    },
                    navArgument(Routes.Args.CREATE_CLASSWORK_TYPE) {
                        type = NavType.StringType
                    },
                    navArgument(Routes.Args.CREATE_CLASSWORK_ID) {
                        nullable = true
                        type = NavType.StringType
                    },
                ),
        ) { backStackEntry ->
            val classworkId = backStackEntry.arguments?.getString(Routes.Args.CREATE_CLASSWORK_ID)

            CreateClassworkScreen(
                snackbarHostState = snackbarHostState,
                courseName = course.name.orEmpty(),
                classworkId = classworkId,
                onCreateClassworkSuccess = {
                    navController.navigateUp()
                },
                onBackPressed = {
                    navController.navigateUp()
                },
            )
        }
        composable(
            route = "${Screen.CreateAnnouncementScreen.route}${Routes.Args.CREATE_ANNOUNCEMENT_SCREEN}",
            arguments =
                listOf(
                    navArgument(Routes.Args.CREATE_ANNOUNCEMENT_COURSE_ID) {
                        type = NavType.StringType
                    },
                    navArgument(Routes.Args.CREATE_ANNOUNCEMENT_ID) {
                        nullable = true
                        type = NavType.StringType
                    },
                ),
        ) { backStackEntry ->
            val announcementId =
                backStackEntry.arguments?.getString(Routes.Args.CREATE_ANNOUNCEMENT_ID)

            CreateAnnouncementScreen(
                snackbarHostState = snackbarHostState,
                courseName = course.name.orEmpty(),
                announcementId = announcementId,
                onCreateAnnouncementSuccess = {
                    navController.navigateUp()
                },
                onBackPressed = {
                    navController.navigateUp()
                },
            )
        }
    }
}
