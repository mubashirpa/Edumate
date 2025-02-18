package app.edumate.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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
import app.edumate.presentation.viewStudentSubmission.ViewStudentSubmissionScreen
import app.edumate.presentation.viewStudentSubmission.ViewStudentSubmissionViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun CourseDetailsNavHost(
    navController: NavHostController,
    courseWithMembers: CourseWithMembers,
    currentUserRole: CourseUserRole,
    onNavigateUp: () -> Unit,
    onNavigateToCourseSettings: (id: String) -> Unit,
    onNavigateToImageViewer: (url: String, title: String?) -> Unit,
    onNavigateToPdfViewer: (url: String, title: String?) -> Unit,
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
                commentsUiState = viewModel.commentsUiState,
                commentsOnEvent = viewModel::onEvent,
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
                onNavigateToCreateCourseWork = { workType, id ->
                    navController.navigate(
                        Screen.CreateCourseWork(
                            courseId = courseWithMembers.id,
                            courseWorkType = workType,
                            courseWorkId = id,
                        ),
                    )
                },
                onNavigateToViewCourseWork = {
                    navController.navigate(
                        Screen.ViewCourseWork(
                            courseId = courseWithMembers.id,
                            courseWorkId = it,
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
        composable<Screen.CreateCourseWork> {
            CreateCourseWorkScreen(
                courseName = courseWithMembers.name.orEmpty(),
                onNavigateUp = navController::navigateUp,
                onCreateCourseWorkComplete = {
                    navController.previousBackStackEntry
                        ?.savedStateHandle[Navigation.Args.CREATE_COURSE_WORK_SUCCESS] = true
                    navController.navigateUp()
                },
            )
        }
        composable<Screen.ViewCourseWork> {
            val viewModel: ViewCourseWorkViewModel = koinViewModel()
            ViewCourseWorkScreen(
                uiState = viewModel.uiState,
                onEvent = viewModel::onEvent,
                currentUserRole = currentUserRole,
                commentsUiState = viewModel.commentsUiState,
                commentsOnEvent = viewModel::onEvent,
                onNavigateUp = navController::navigateUp,
                onNavigateToImageViewer = onNavigateToImageViewer,
                onNavigateToViewStudentSubmission = { courseWorkId, studentId ->
                    navController.navigate(
                        Screen.ViewStudentSubmission(
                            courseId = courseWithMembers.id,
                            courseWorkId = courseWorkId,
                            studentId = studentId,
                        ),
                    )
                },
            )
        }
        composable<Screen.ViewStudentSubmission> {
            val viewModel: ViewStudentSubmissionViewModel = koinViewModel()
            ViewStudentSubmissionScreen(
                uiState = viewModel.uiState,
                onEvent = viewModel::onEvent,
                currentUserRole = currentUserRole,
                commentsUiState = viewModel.commentsUiState,
                commentsOnEvent = viewModel::onEvent,
                onNavigateUp = navController::navigateUp,
                onNavigateToImageViewer = onNavigateToImageViewer,
            )
        }
    }
}
