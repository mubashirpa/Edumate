package edumate.app.presentation.enrolled

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
import edumate.app.presentation.components.ErrorScreen
import edumate.app.presentation.components.ProgressDialog
import edumate.app.presentation.enrolled.components.EnrolledListItem
import edumate.app.presentation.enrolled.components.UnEnrollDialog
import edumate.app.R.string as Strings

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun EnrolledScreen(
    uiState: EnrolledUiState,
    onEvent: (EnrolledUiEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    innerPadding: PaddingValues,
    refreshUsingActionButton: Boolean,
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
                onEvent(EnrolledUiEvent.Refresh)
            },
        )

    LaunchedEffect(refreshUsingActionButton) {
        if (refreshUsingActionButton) {
            onEvent(EnrolledUiEvent.Refresh)
        }
    }

    uiState.userMessage?.let { userMessage ->
        LaunchedEffect(userMessage) {
            snackbarHostState.showSnackbar(userMessage.asString(context))
            // Once the message is displayed and dismissed, notify the
            onEvent(EnrolledUiEvent.UserMessageShown)
        }
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .pullRefresh(refreshState),
    ) {
        when (val enrolledCoursesResult = uiState.enrolledCoursesResult) {
            is Result.Empty -> {
                // Nothing is shown
            }

            is Result.Error -> {
                ErrorScreen(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                    errorMessage = enrolledCoursesResult.message!!.asString(),
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
                val courses = enrolledCoursesResult.data
                if (courses.isNullOrEmpty()) {
                    ErrorScreen(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                        errorMessage = stringResource(id = Strings.join_a_class_to_get_started),
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = contentPadding,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        content = {
                            items(
                                items = enrolledCoursesResult.data,
                                key = { it.id!! },
                            ) { course ->
                                EnrolledListItem(
                                    course = course,
                                    modifier = Modifier.animateItemPlacement(),
                                    onUnEnrollClick = {
                                        onEvent(EnrolledUiEvent.OnOpenUnEnrolDialogChange(it))
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

    UnEnrollDialog(
        onDismissRequest = {
            onEvent(EnrolledUiEvent.OnOpenUnEnrolDialogChange(null))
        },
        openDialog = uiState.unEnrollCourseId != null,
        onConfirmClick = {
            onEvent(EnrolledUiEvent.UnEnroll(uiState.unEnrollCourseId!!))
        },
    )

    ProgressDialog(openDialog = uiState.openProgressDialog)
}
