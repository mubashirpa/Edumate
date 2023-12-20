package edumate.app.presentation.teaching.screen

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edumate.app.core.DataState
import edumate.app.core.ext.supportWideScreen
import edumate.app.presentation.components.ErrorScreen
import edumate.app.presentation.components.LoadingIndicator
import edumate.app.presentation.components.ProgressDialog
import edumate.app.presentation.teaching.TeachingUiEvent
import edumate.app.presentation.teaching.TeachingViewModel
import edumate.app.presentation.teaching.screen.components.DeleteCourseDialog
import edumate.app.presentation.teaching.screen.components.TeachingListItem

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun TeachingScreen(
    viewModel: TeachingViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState,
    contentPadding: PaddingValues,
    refreshUsingActionButton: Boolean,
    navigateToCreateClass: (courseId: String) -> Unit,
    navigateToClassDetails: (courseId: String) -> Unit,
) {
    val context = LocalContext.current
    val refreshState =
        rememberPullRefreshState(
            refreshing = viewModel.uiState.refreshing,
            onRefresh = {
                viewModel.onEvent(TeachingUiEvent.OnRefresh)
            },
        )

    LaunchedEffect(refreshUsingActionButton) {
        if (refreshUsingActionButton) {
            viewModel.onEvent(TeachingUiEvent.OnRefresh)
        }
    }

    viewModel.uiState.userMessage?.let { userMessage ->
        LaunchedEffect(userMessage) {
            snackbarHostState.showSnackbar(userMessage.asString(context))
            // Once the message is displayed and dismissed, notify the ViewModel.
            viewModel.onEvent(TeachingUiEvent.UserMessageShown)
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
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                            .navigationBarsPadding(),
                    errorMessage = dataState.message.asString(),
                )
            }

            is DataState.ERROR -> {
                ErrorScreen(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                            .navigationBarsPadding(),
                    errorMessage = dataState.message.asString(),
                )
            }

            DataState.LOADING -> {
                LoadingIndicator(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .navigationBarsPadding(),
                )
            }

            DataState.SUCCESS -> {
                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .supportWideScreen(),
                    contentPadding = contentPadding,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    content = {
                        itemsIndexed(
                            viewModel.uiState.courses,
                            key = { _, item -> item.id },
                        ) { index, course ->
                            TeachingListItem(
                                course = course,
                                index = index,
                                modifier = Modifier.animateItemPlacement(),
                                onShareClick = { share(context, it) },
                                onEditClick = navigateToCreateClass,
                                onDeleteClick = {
                                    viewModel.onEvent(
                                        TeachingUiEvent.OnOpenDeleteCourseDialogChange(
                                            it,
                                        ),
                                    )
                                },
                                onClick = navigateToClassDetails,
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

    DeleteCourseDialog(
        onDismissRequest = {
            viewModel.onEvent(TeachingUiEvent.OnOpenDeleteCourseDialogChange(null))
        },
        courseId = viewModel.uiState.deleteCourseId,
        onConfirmClick = {
            viewModel.onEvent(TeachingUiEvent.OnDeleteCourse(it))
        },
    )

    ProgressDialog(openDialog = viewModel.uiState.openProgressDialog)
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
