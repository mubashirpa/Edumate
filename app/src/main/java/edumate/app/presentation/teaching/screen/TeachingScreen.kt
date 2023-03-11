package edumate.app.presentation.teaching.screen

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
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

@Composable
fun TeachingScreen(
    viewModel: TeachingViewModel = hiltViewModel(),
    navigateToCreateClass: (courseId: String) -> Unit,
    navigateToClassDetails: (name: String, courseId: String) -> Unit
) {
    val context = LocalContext.current

    when {
        viewModel.uiState.loading -> {
            LoadingIndicator()
        }
        viewModel.uiState.error != null -> {
            ErrorScreen(
                onRetry = {
                    viewModel.onEvent(TeachingUiEvent.FetchClasses)
                }
            )
        }
        viewModel.uiState.classes.isEmpty() -> {
            ErrorScreen(errorMessage = stringResource(id = Strings.add_a_class_to_get_started))
        }
        else -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                content = {
                    items(viewModel.uiState.classes) { course ->
                        TeachingListItem(
                            course = course,
                            onShareClick = {
                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, it)
                                    type = "text/plain"
                                }

                                val shareIntent = Intent.createChooser(sendIntent, null)
                                context.startActivity(shareIntent)
                            },
                            onEditClick = navigateToCreateClass,
                            onClick = navigateToClassDetails
                        )
                    }
                }
            )
        }
    }
}