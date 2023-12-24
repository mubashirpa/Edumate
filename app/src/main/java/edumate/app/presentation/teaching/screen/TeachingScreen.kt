package edumate.app.presentation.teaching.screen

import android.content.Context
import android.content.Intent
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
import edumate.app.core.Resource
import edumate.app.presentation.components.ErrorScreen
import edumate.app.presentation.components.LoadingIndicator
import edumate.app.presentation.components.ProgressDialog
import edumate.app.presentation.teaching.TeachingUiEvent
import edumate.app.presentation.teaching.TeachingUiState
import edumate.app.presentation.teaching.screen.components.DeleteCourseDialog
import edumate.app.presentation.teaching.screen.components.TeachingListItem
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
                onEvent(TeachingUiEvent.OnRefresh)
            },
        )

    LaunchedEffect(refreshUsingActionButton) {
        if (refreshUsingActionButton) {
            onEvent(TeachingUiEvent.OnRefresh)
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
        when (val teachingCoursesResource = uiState.teachingCoursesResource) {
            is Resource.Unknown -> {
                // Nothing is shown
            }

            is Resource.Error -> {
                ErrorScreen(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    errorMessage = teachingCoursesResource.message!!.asString(),
                )
            }

            is Resource.Loading -> {
                LoadingIndicator(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                )
            }

            is Resource.Success -> {
                val courses = teachingCoursesResource.data
                if (courses.isNullOrEmpty()) {
                    ErrorScreen(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                        errorMessage = stringResource(id = Strings.add_a_class_to_get_started),
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = contentPadding,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        content = {
                            itemsIndexed(
                                teachingCoursesResource.data,
                                key = { _, item -> item.id },
                            ) { index, course ->
                                TeachingListItem(
                                    course = course,
                                    index = index,
                                    modifier = Modifier.animateItemPlacement(),
                                    onShareClick = { share(context, it) },
                                    onEditClick = navigateToCreateClass,
                                    onDeleteClick = {
                                        onEvent(TeachingUiEvent.OnOpenDeleteCourseDialogChange(it))
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

    DeleteCourseDialog(
        onDismissRequest = {
            onEvent(TeachingUiEvent.OnOpenDeleteCourseDialogChange(null))
        },
        courseId = uiState.deleteCourseId,
        onConfirmClick = {
            onEvent(TeachingUiEvent.OnDeleteCourse(it))
        },
    )

    ProgressDialog(openDialog = uiState.openProgressDialog)
}

private fun share(
    context: Context,
    text: String,
) {
    val sendIntent: Intent =
        Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}
