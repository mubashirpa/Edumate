package edumate.app.presentation.teaching.screen

import android.content.Context
import android.content.Intent
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
import edumate.app.presentation.components.ErrorScreen
import edumate.app.presentation.components.LoadingIndicator
import edumate.app.presentation.teaching.TeachingUiEvent
import edumate.app.presentation.teaching.TeachingViewModel
import edumate.app.presentation.teaching.screen.components.TeachingListItem

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TeachingScreen(
    viewModel: TeachingViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState,
    contentPadding: PaddingValues,
    navigateToCreateClass: (courseId: String) -> Unit,
    navigateToClassDetails: (courseId: String) -> Unit
) {
    val context = LocalContext.current
    val refreshState = rememberPullRefreshState(
        refreshing = viewModel.uiState.refreshing,
        onRefresh = {
            viewModel.onEvent(TeachingUiEvent.OnRefresh)
        }
    )

    viewModel.uiState.userMessage?.let { userMessage ->
        LaunchedEffect(userMessage) {
            snackbarHostState.showSnackbar(userMessage.asString(context))
            // Once the message is displayed and dismissed, notify the ViewModel.
            viewModel.onEvent(TeachingUiEvent.UserMessageShown)
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
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = contentPadding,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    content = {
                        items(viewModel.uiState.courses) { course ->
                            TeachingListItem(
                                course = course,
                                onShareClick = {
                                    share(context, it)
                                },
                                onEditClick = navigateToCreateClass,
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
}

private fun share(context: Context, text: String) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}