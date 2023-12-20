package edumate.app.presentation.student_work.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edumate.app.core.DataState
import edumate.app.domain.model.course_work.CourseWork
import edumate.app.domain.model.user_profiles.UserProfile
import edumate.app.presentation.components.ErrorScreen
import edumate.app.presentation.components.LoadingIndicator
import edumate.app.presentation.student_work.StudentWorkUiEvent
import edumate.app.presentation.student_work.StudentWorkViewModel
import edumate.app.presentation.student_work.screen.components.StudentWorkListItem

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StudentWorkScreen(
    viewModel: StudentWorkViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState,
    courseWork: CourseWork,
    refreshUsingActionButton: Boolean,
    navigateToViewStudentWork: (studentWorkId: String?, assignedStudent: UserProfile) -> Unit,
) {
    val context = LocalContext.current
    val refreshState =
        rememberPullRefreshState(
            refreshing = viewModel.uiState.refreshing,
            onRefresh = { viewModel.onEvent(StudentWorkUiEvent.OnRefresh) },
        )

    LaunchedEffect(courseWork.id) {
        viewModel.onEvent(StudentWorkUiEvent.OnInit(courseWork.courseId, courseWork.id))
    }

    LaunchedEffect(refreshUsingActionButton) {
        if (refreshUsingActionButton) {
            viewModel.onEvent(StudentWorkUiEvent.OnRefresh)
        }
    }

    viewModel.uiState.userMessage?.let { userMessage ->
        LaunchedEffect(userMessage) {
            snackbarHostState.showSnackbar(userMessage.asString(context))
            // Once the message is displayed and dismissed, notify the ViewModel.
            viewModel.onEvent(StudentWorkUiEvent.UserMessageShown)
        }
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .pullRefresh(refreshState),
    ) {
        when (val dataState = viewModel.uiState.dataState) {
            is DataState.EMPTY -> {
                ErrorScreen(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                    errorMessage = dataState.message.asString(),
                )
            }

            is DataState.ERROR -> {
                ErrorScreen(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                    errorMessage = dataState.message.asString(),
                )
            }

            DataState.LOADING -> {
                LoadingIndicator(modifier = Modifier.fillMaxSize())
            }

            DataState.SUCCESS -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 10.dp),
                    content = {
                        items(viewModel.uiState.assignedStudents) { assignedStudent ->
                            val studentSubmission =
                                viewModel.uiState.studentSubmissions.find { it.userId == assignedStudent.id }

                            StudentWorkListItem(
                                courseWork = courseWork,
                                assignedStudent = assignedStudent,
                                studentSubmission = studentSubmission,
                                onClick = { studentWorkId ->
                                    navigateToViewStudentWork(studentWorkId, assignedStudent)
                                },
                            )
                        }
                    },
                )
            }

            DataState.UNKNOWN -> {}
        }

        PullRefreshIndicator(
            viewModel.uiState.refreshing,
            refreshState,
            Modifier.align(Alignment.TopCenter),
        )
    }
}
