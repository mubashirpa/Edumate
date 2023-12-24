package edumate.app.presentation.enrolled.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import edumate.app.core.utils.ResourceNew
import edumate.app.presentation.components.ErrorScreen
import edumate.app.presentation.components.LoadingIndicator
import edumate.app.presentation.components.ProgressDialog
import edumate.app.presentation.enrolled.EnrolledUiEvent
import edumate.app.presentation.enrolled.EnrolledUiState
import edumate.app.presentation.enrolled.screen.components.EnrolledListItem
import edumate.app.presentation.enrolled.screen.components.UnEnrolDialog
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
    val contentPadding =
        PaddingValues(
            start = 16.dp,
            top = 12.dp,
            end = 16.dp,
            bottom = innerPadding.calculateBottomPadding() + 88.dp, // FloatingActionButton height including margin,
        )
    val refreshState =
        rememberPullRefreshState(
            refreshing = uiState.refreshing,
            onRefresh = {
                onEvent(EnrolledUiEvent.OnRefresh)
            },
        )

    LaunchedEffect(refreshUsingActionButton) {
        if (refreshUsingActionButton) {
            onEvent(EnrolledUiEvent.OnRefresh)
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
        when (val enrolledCoursesResource = uiState.enrolledCoursesResource) {
            is ResourceNew.Unknown -> {
                // Nothing is shown
            }

            is ResourceNew.Error -> {
                ErrorScreen(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    errorMessage = enrolledCoursesResource.message!!.asString(),
                )
            }

            is ResourceNew.Loading -> {
                LoadingIndicator(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                )
            }

            is ResourceNew.Success -> {
                val courses = enrolledCoursesResource.data
                if (courses.isNullOrEmpty()) {
                    ErrorScreen(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                        errorMessage = stringResource(id = Strings.join_a_class_to_get_started),
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = contentPadding,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        content = {
                            itemsIndexed(
                                enrolledCoursesResource.data,
                                key = { _, item -> item.id },
                            ) { index, course ->
                                EnrolledListItem(
                                    course = course,
                                    index = index,
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
            uiState.refreshing,
            refreshState,
            Modifier.align(Alignment.TopCenter),
        )
    }

    UnEnrolDialog(
        onDismissRequest = {
            onEvent(EnrolledUiEvent.OnOpenUnEnrolDialogChange(null))
        },
        courseId = uiState.unEnrolCourseId,
        onConfirmClick = {
            onEvent(EnrolledUiEvent.OnUnEnroll(it))
        },
    )

    ProgressDialog(openDialog = uiState.openProgressDialog)
}
