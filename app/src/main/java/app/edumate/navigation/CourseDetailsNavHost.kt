package app.edumate.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import app.edumate.core.Navigation
import app.edumate.core.ext.GetOnceResult
import app.edumate.domain.model.course.CourseWithMembers
import app.edumate.presentation.courseDetails.CourseUserRole
import app.edumate.presentation.courseWork.CourseWorkScreen
import app.edumate.presentation.courseWork.CourseWorkUiEvent
import app.edumate.presentation.courseWork.CourseWorkViewModel
import app.edumate.presentation.createCourseWork.CreateCourseWorkScreen
import app.edumate.presentation.people.PeopleScreen
import app.edumate.presentation.stream.StreamScreen
import app.edumate.presentation.stream.StreamViewModel
import app.edumate.presentation.viewCourseWork.ViewCourseWorkScreen
import app.edumate.presentation.viewCourseWork.ViewCourseWorkViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun CourseDetailsNavHost(
    navController: NavHostController,
    courseWithMembers: CourseWithMembers,
    currentUserRole: CourseUserRole,
    onNavigateUp: () -> Unit,
    onNavigateToCourseSettings: (id: String) -> Unit,
    onNavigateToImageViewer: (url: String, title: String?) -> Unit,
    onLeaveCourse: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Stream(courseWithMembers.id!!),
        modifier = modifier,
    ) {
        composable<Screen.Stream> {
            val viewModel: StreamViewModel = koinViewModel()
            StreamScreen(
                uiState = viewModel.uiState,
                onEvent = viewModel::onEvent,
                courseWithMembers = courseWithMembers,
                currentUserRole = currentUserRole,
                commentsBottomSheetUiState = viewModel.commentsBottomSheetUiState,
                onNavigateUp = onNavigateUp,
                onNavigateToCourseSettings = onNavigateToCourseSettings,
                onNavigateToImageViewer = onNavigateToImageViewer,
            )
        }
        composable<Screen.CourseWork> {
            val viewModel: CourseWorkViewModel = koinViewModel()

            navController.GetOnceResult<Boolean>(Navigation.Args.CREATE_COURSE_WORK_SUCCESS) { refresh ->
                if (refresh) {
                    viewModel.onEvent(CourseWorkUiEvent.Refresh)
                }
            }

            CourseWorkScreen(
                uiState = viewModel.uiState,
                onEvent = viewModel::onEvent,
                courseWithMembers = courseWithMembers,
                currentUserRole = currentUserRole,
                onNavigateUp = onNavigateUp,
                onNavigateToCreateClasswork = { workType, id ->
                    navController.navigate(
                        Screen.CreateCourseWork(
                            courseId = courseWithMembers.id,
                            workType = workType,
                            id = id,
                        ),
                    )
                },
                onNavigateToViewClasswork = {
                    navController.navigate(
                        Screen.ViewCourseWork(
                            id = it,
                            courseId = courseWithMembers.id,
                            isCurrentUserStudent = currentUserRole == CourseUserRole.Student,
                        ),
                    )
                },
            )
        }
        composable<Screen.People> {
            PeopleScreen(
                courseWithMembers = courseWithMembers,
                currentUserRole = currentUserRole,
                onNavigateUp = onNavigateUp,
                onLeaveCourseComplete = onLeaveCourse,
            )
        }
        composable<Screen.CreateCourseWork> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.CreateCourseWork>()
            CreateCourseWorkScreen(
                courseName = courseWithMembers.name.orEmpty(),
                onNavigateUp = navController::navigateUp,
                onCreateCourseWorkComplete = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle[Navigation.Args.CREATE_COURSE_WORK_SUCCESS] = true
                    navController.navigateUp()
                },
                courseWorkId = route.id,
            )
        }
        composable<Screen.ViewCourseWork> {
            val viewModel: ViewCourseWorkViewModel = koinViewModel()
            ViewCourseWorkScreen(
                uiState = viewModel.uiState,
                onEvent = viewModel::onEvent,
                currentUserRole = currentUserRole,
                onNavigateUp = navController::navigateUp,
                onNavigateToImageViewer = onNavigateToImageViewer,
                onNavigateToViewStudentSubmission = { /*TODO*/ },
            )
        }
    }
}
