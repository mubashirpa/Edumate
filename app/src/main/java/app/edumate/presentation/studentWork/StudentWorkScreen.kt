package app.edumate.presentation.studentWork

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.edumate.R
import app.edumate.core.Result
import app.edumate.domain.model.courseWork.CourseWork
import app.edumate.presentation.components.ErrorScreen
import app.edumate.presentation.components.LoadingScreen
import app.edumate.presentation.studentWork.components.StudentWorkListItem
import org.koin.androidx.compose.koinViewModel

@Composable
fun StudentWorkScreen(
    snackbarHostState: SnackbarHostState,
    courseWork: CourseWork,
    isRefreshing: Boolean,
    onNavigateToViewStudentSubmission: (userId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StudentWorkViewModel = koinViewModel(),
) {
    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            viewModel.onEvent(StudentWorkUiEvent.Refresh)
        }
    }

    StudentWorkScreenContent(
        uiState = viewModel.uiState,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState,
        courseWork = courseWork,
        onNavigateToViewStudentSubmission = onNavigateToViewStudentSubmission,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StudentWorkScreenContent(
    uiState: StudentWorkUiState,
    onEvent: (StudentWorkUiEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    courseWork: CourseWork,
    onNavigateToViewStudentSubmission: (userId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    uiState.userMessage?.let { userMessage ->
        LaunchedEffect(userMessage) {
            snackbarHostState.showSnackbar(userMessage.asString(context))
            // Once the message is displayed and dismissed, notify the ViewModel.
            onEvent(StudentWorkUiEvent.UserMessageShown)
        }
    }

    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = {
            onEvent(StudentWorkUiEvent.Refresh)
        },
        modifier = modifier,
    ) {
        when (val studentSubmissionsResult = uiState.studentSubmissionsResult) {
            is Result.Empty -> {}

            is Result.Error -> {
                ErrorScreen(
                    onRetryClick = {
                        onEvent(StudentWorkUiEvent.Retry)
                    },
                    modifier = Modifier.fillMaxSize(),
                    errorMessage = studentSubmissionsResult.message!!.asString(),
                )
            }

            is Result.Loading -> {
                LoadingScreen()
            }

            is Result.Success -> {
                val studentSubmissions = studentSubmissionsResult.data!!

                if (studentSubmissions.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 12.dp),
                        content = {
                            items(
                                items = studentSubmissions,
                                key = { it.user!!.id!! },
                            ) { submission ->
                                StudentWorkListItem(
                                    courseWork = courseWork,
                                    studentSubmission = submission,
                                    onClick = onNavigateToViewStudentSubmission,
                                    modifier = Modifier.animateItem(),
                                )
                            }
                        },
                    )
                } else {
                    ErrorScreen(
                        modifier = Modifier.fillMaxSize(),
                        errorMessage = stringResource(id = R.string.this_has_not_been_assigned_to_any_students),
                    )
                }
            }
        }
    }
}
