package edumate.app.presentation.enrolled.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
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
import edumate.app.presentation.enrolled.EnrolledUiEvent
import edumate.app.presentation.enrolled.EnrolledViewModel
import edumate.app.presentation.enrolled.screen.components.EnrolledListItem

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EnrolledScreen(
    viewModel: EnrolledViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState,
    contentPadding: PaddingValues,
    navigateToClassDetails: (courseId: String) -> Unit
) {
    val context = LocalContext.current
    val refreshState = rememberPullRefreshState(
        refreshing = viewModel.uiState.refreshing,
        onRefresh = {
            viewModel.onEvent(EnrolledUiEvent.OnRefresh)
        }
    )

    viewModel.uiState.userMessage?.let { userMessage ->
        LaunchedEffect(userMessage) {
            snackbarHostState.showSnackbar(userMessage.asString(context))
            // Once the message is displayed and dismissed, notify the ViewModel.
            viewModel.onEvent(EnrolledUiEvent.UserMessageShown)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(refreshState)
    ) {
        when (val dataState = viewModel.uiState.dataState) {
            is DataState.EMPTY -> {
                ErrorScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding(),
                    errorMessage = dataState.message.asString()
                )
            }

            is DataState.ERROR -> {
                ErrorScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding(),
                    errorMessage = dataState.message.asString()
                )
            }

            DataState.LOADING -> {
                LoadingIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding()
                )
            }

            DataState.SUCCESS -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .supportWideScreen(),
                    contentPadding = contentPadding,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    content = {
                        items(viewModel.uiState.courses) { course ->
                            EnrolledListItem(
                                course = course,
                                onUnEnrollClick = {
                                    viewModel.onEvent(EnrolledUiEvent.Unenroll(it))
                                },
                                onClick = navigateToClassDetails
                            )
                        }
                    }
                )
            }

            DataState.UNKNOWN -> {}
        }

        PullRefreshIndicator(
            viewModel.uiState.refreshing,
            refreshState,
            Modifier.align(Alignment.TopCenter)
        )
    }

    ProgressDialog(openDialog = viewModel.uiState.openProgressDialog)
}