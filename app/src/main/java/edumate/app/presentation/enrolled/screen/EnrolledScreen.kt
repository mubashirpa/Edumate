package edumate.app.presentation.enrolled.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edumate.app.R.string as Strings
import edumate.app.presentation.components.ErrorScreen
import edumate.app.presentation.components.LoadingIndicator
import edumate.app.presentation.components.ProgressDialog
import edumate.app.presentation.enrolled.EnrolledUiEvent
import edumate.app.presentation.enrolled.EnrolledViewModel
import edumate.app.presentation.enrolled.screen.components.EnrolledListItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EnrolledScreen(
    viewModel: EnrolledViewModel = hiltViewModel(),
    navigateToClassDetails: (courseId: String) -> Unit
) {
    val context = LocalContext.current
    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }

    fun refresh() = refreshScope.launch {
        refreshing = true
        viewModel.onEvent(EnrolledUiEvent.FetchClasses)
        delay(1500)
        refreshing = false
    }

    val state = rememberPullRefreshState(refreshing, ::refresh)

    viewModel.uiState.userMessage?.let { userMessage ->
        LaunchedEffect(userMessage) {
            // TODO("Convert to snackbar")
            Toast.makeText(context, userMessage.asString(context), Toast.LENGTH_LONG).show()
            // Once the message is displayed and dismissed, notify the ViewModel.
            viewModel.onEvent(EnrolledUiEvent.UserMessageShown)
        }
    }

    when {
        viewModel.uiState.loading -> {
            LoadingIndicator()
        }
        viewModel.uiState.error != null -> {
            ErrorScreen(
                onRetry = {
                    viewModel.onEvent(EnrolledUiEvent.FetchClasses)
                }
            )
        }
        viewModel.uiState.classes.isEmpty() -> {
            ErrorScreen(
                errorMessage = stringResource(id = Strings.join_a_class_to_get_started)
            )
        }
        else -> {
            Box(modifier = Modifier.pullRefresh(state)) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    content = {
                        items(viewModel.uiState.classes) { course ->
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

                PullRefreshIndicator(refreshing, state, Modifier.align(Alignment.TopCenter))
            }
        }
    }

    ProgressDialog(openDialog = viewModel.uiState.openProgressDialog)
}