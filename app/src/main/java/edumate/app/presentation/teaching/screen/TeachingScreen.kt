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
import edumate.app.presentation.teaching.TeachingUiEvent
import edumate.app.presentation.teaching.TeachingViewModel
import edumate.app.presentation.teaching.screen.components.TeachingListItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TeachingScreen(
    viewModel: TeachingViewModel = hiltViewModel(),
    contentPadding: PaddingValues,
    navigateToCreateClass: (courseId: String) -> Unit,
    navigateToClassDetails: (courseId: String) -> Unit
) {
    val context = LocalContext.current
    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }

    fun refresh() = refreshScope.launch {
        refreshing = true
        viewModel.onEvent(TeachingUiEvent.FetchClasses)
        delay(1500)
        refreshing = false
    }

    val state = rememberPullRefreshState(refreshing, ::refresh)

    when {
        viewModel.uiState.loading -> {
            LoadingIndicator(modifier = Modifier.fillMaxSize())
        }
        viewModel.uiState.error != null -> {
            ErrorScreen(
                modifier = Modifier.fillMaxSize(),
                onRetry = {
                    viewModel.onEvent(TeachingUiEvent.FetchClasses)
                }
            )
        }
        viewModel.uiState.classes.isEmpty() -> {
            ErrorScreen(
                modifier = Modifier.fillMaxSize(),
                errorMessage = stringResource(id = Strings.add_a_class_to_get_started)
            )
        }
        else -> {
            Box(modifier = Modifier.pullRefresh(state)) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = contentPadding,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    content = {
                        items(viewModel.uiState.classes) { course ->
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

                PullRefreshIndicator(refreshing, state, Modifier.align(Alignment.TopCenter))
            }
        }
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