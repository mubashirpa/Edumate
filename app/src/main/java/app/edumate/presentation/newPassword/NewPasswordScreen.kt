package app.edumate.presentation.newPassword

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import app.edumate.R
import app.edumate.presentation.components.PasswordField
import app.edumate.presentation.components.ProgressDialog
import app.edumate.presentation.theme.EdumateTheme
import kotlinx.coroutines.flow.filter
import org.koin.androidx.compose.koinViewModel

@Composable
fun NewPasswordScreen(
    email: String,
    onUpdatePasswordComplete: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NewPasswordViewModel = koinViewModel(),
) {
    val currentOnUpdatePasswordComplete by rememberUpdatedState(onUpdatePasswordComplete)
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(viewModel, lifecycle) {
        snapshotFlow { viewModel.uiState }
            .filter { it.isUpdatePassword }
            .flowWithLifecycle(lifecycle)
            .collect {
                currentOnUpdatePasswordComplete()
            }
    }

    NewPasswordContent(
        email = email,
        uiState = viewModel.uiState,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun NewPasswordContent(
    email: String,
    uiState: NewPasswordUiState,
    onEvent: (NewPasswordUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }

    uiState.userMessage?.let { userMessage ->
        LaunchedEffect(userMessage) {
            snackbarHostState.showSnackbar(userMessage.asString(context))
            // Once the message is displayed and dismissed, notify the ViewModel.
            onEvent(NewPasswordUiEvent.UserMessageShown)
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        content = { innerPadding ->
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(innerPadding)
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.choose_new_password),
                    style = MaterialTheme.typography.headlineSmall,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.update_password_description, email),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                )
                Spacer(modifier = Modifier.height(24.dp))
                PasswordField(
                    state = uiState.password,
                    focusManager = focusManager,
                    keyboardController = keyboardController,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                    autofillType = listOf(AutofillType.NewPassword),
                    placeholder = {
                        Text(text = stringResource(R.string.new_password))
                    },
                    isError = uiState.passwordError != null,
                    errorMessage = uiState.passwordError?.asString(),
                    imeAction = ImeAction.Next,
                )
                Spacer(modifier = Modifier.height(12.dp))
                PasswordField(
                    state = uiState.repeatedPassword,
                    focusManager = focusManager,
                    keyboardController = keyboardController,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(text = stringResource(R.string.confirm_new_password))
                    },
                    isError = uiState.repeatedPasswordError != null,
                    errorMessage = uiState.repeatedPasswordError?.asString(),
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        onEvent(NewPasswordUiEvent.UpdatePassword)
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                    shape = MaterialTheme.shapes.large,
                ) {
                    Text(
                        text = stringResource(id = R.string.reset_password),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        },
    )

    ProgressDialog(
        text = stringResource(R.string.updating_password),
        open = uiState.openProgressDialog,
        onDismissRequest = {},
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Preview
@Composable
private fun NewPasswordScreenPreview() {
    EdumateTheme {
        NewPasswordContent(
            email = "admin@edumate.app",
            uiState = NewPasswordUiState(),
            onEvent = {},
        )
    }
}
