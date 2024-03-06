package edumate.app.presentation.teaching

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import edumate.app.core.Result
import edumate.app.core.utils.IntentUtils
import edumate.app.presentation.components.ErrorScreen
import edumate.app.presentation.components.ProgressDialog
import edumate.app.presentation.teaching.components.DeleteCourseDialog
import edumate.app.presentation.teaching.components.LeaveCourseDialog
import edumate.app.presentation.teaching.components.TeachingListItem
import edumate.app.R.string as Strings

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun TeachingScreen(
    uiState: TeachingUiState,
    onEvent: (TeachingUiEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    innerPadding: PaddingValues,
    refreshUsingActionButton: Boolean,
    navigateToCreateClass: (courseId: String) -> Unit,
    navigateToClassDetails: (courseId: String) -> Unit,
) {
    val context = LocalContext.current
    val bottomPadding = innerPadding.calculateBottomPadding()
    val contentPadding =
        PaddingValues(
            start = 16.dp,
            top = 12.dp,
            end = 16.dp,
            bottom = bottomPadding + 88.dp,
        )
    val refreshState =
        rememberPullRefreshState(
            refreshing = uiState.isRefreshing,
            onRefresh = {
                onEvent(TeachingUiEvent.Refresh)
            },
        )

    LaunchedEffect(refreshUsingActionButton) {
        if (refreshUsingActionButton) {
            onEvent(TeachingUiEvent.Refresh)
        }
    }

    uiState.userMessage?.let { userMessage ->
        LaunchedEffect(userMessage) {
            snackbarHostState.showSnackbar(userMessage.asString(context))
            // Once the message is displayed and dismissed, notify the ViewModel.
            onEvent(TeachingUiEvent.UserMessageShown)
        }
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .pullRefresh(refreshState),
    ) {
        when (val teachingCoursesResult = uiState.teachingCoursesResult) {
            is Result.Empty -> {
                // Nothing is shown
            }

            is Result.Error -> {
                ErrorScreen(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                    errorMessage = teachingCoursesResult.message!!.asString(),
                )
            }

            is Result.Loading -> {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center),
                ) {
                    CircularProgressIndicator()
                }
            }

            is Result.Success -> {
                val courses = teachingCoursesResult.data
                if (courses.isNullOrEmpty()) {
                    ErrorScreen(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                        errorMessage = stringResource(id = Strings.add_a_class_to_get_started),
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = contentPadding,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        content = {
                            items(
                                items = courses,
                                key = { it.id!! },
                            ) { course ->
                                TeachingListItem(
                                    course = course,
                                    modifier = Modifier.animateItemPlacement(),
                                    userId = uiState.userId.orEmpty(),
                                    onShareClick = {
                                        IntentUtils.shareText(context, it)
                                    },
                                    onEditClick = navigateToCreateClass,
                                    onDeleteClick = {
                                        onEvent(
                                            TeachingUiEvent.OnOpenDeleteCourseDialogChange(
                                                course,
                                            ),
                                        )
                                    },
                                    onLeaveClick = {
                                        onEvent(TeachingUiEvent.OnOpenLeaveCourseDialogChange(course))
                                    },
                                    onClick = navigateToClassDetails,
                                )
                            }
                        },
                    )
                }
            }
        }

        PullRefreshIndicator(
            refreshing = uiState.isRefreshing,
            state = refreshState,
            modifier = Modifier.align(Alignment.TopCenter),
        )
    }

    DeleteCourseDialog(
        onDismissRequest = {
            onEvent(TeachingUiEvent.OnOpenDeleteCourseDialogChange(null))
        },
        open = uiState.deleteCourse != null,
        onConfirmButtonClick = {
            onEvent(TeachingUiEvent.DeleteCourse(uiState.deleteCourse!!.id))
        },
    )

    LeaveCourseDialog(
        onDismissRequest = {
            onEvent(TeachingUiEvent.OnOpenLeaveCourseDialogChange(null))
        },
        open = uiState.leaveCourse != null,
        name = uiState.leaveCourse?.name.orEmpty(),
        onConfirmButtonClick = {
            onEvent(TeachingUiEvent.LeaveCourse(uiState.leaveCourse!!.id))
        },
    )

    ProgressDialog(openDialog = uiState.openProgressDialog)
}
