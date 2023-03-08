package edumate.app.presentation.enrolled.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

@Composable
fun EnrolledScreen(
    viewModel: EnrolledViewModel = hiltViewModel(),
    navigateToClassDetails: (courseId: String) -> Unit
) {
    val context = LocalContext.current

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
            ErrorScreen(errorMessage = stringResource(id = Strings.join_a_class_to_get_started))
        }
        else -> {
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
        }
    }

    ProgressDialog(openDialog = viewModel.uiState.openProgressDialog)
}