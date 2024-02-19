package edumate.app.presentation.recover

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.flowWithLifecycle
import edumate.app.presentation.components.EmailField
import edumate.app.presentation.components.ProgressDialog
import edumate.app.presentation.ui.theme.EdumateTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import edumate.app.R.string as Strings

@Composable
fun RecoverScreen(
    viewModel: RecoverViewModel = hiltViewModel(),
    rootSnackbarHostState: SnackbarHostState,
    rootSnackbarScope: CoroutineScope,
    onPasswordResetEmailSent: () -> Unit,
) {
    val context = LocalContext.current
    val currentOnPasswordResetEmailSent by rememberUpdatedState(onPasswordResetEmailSent)
    val snackbarHostState = remember { SnackbarHostState() }
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(viewModel, lifecycle) {
        // Whenever the uiState changes, check if the password reset email sent and
        // call the `onRecoverSuccess` event when `lifecycle` is at least STARTED
        snapshotFlow { viewModel.uiState }
            .filter { it.isPasswordResetEmailSend }
            .flowWithLifecycle(lifecycle)
            .collect {
                rootSnackbarScope.launch {
                    rootSnackbarHostState.showSnackbar(
                        context.getString(
                            Strings.success_send_password_reset_email,
                            viewModel.uiState.email,
                        ),
                    )
                }
                currentOnPasswordResetEmailSent()
            }
    }

    viewModel.uiState.userMessage?.let { userMessage ->
        LaunchedEffect(userMessage) {
            snackbarHostState.showSnackbar(userMessage.asString(context))
            // Once the message is displayed and dismissed, notify the ViewModel.
            viewModel.onEvent(RecoverUiEvent.UserMessageShown)
        }
    }

    RecoverScreenContent(
        uiState = viewModel.uiState,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState,
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun RecoverScreenContent(
    uiState: RecoverUiState,
    onEvent: (RecoverUiEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    val focusRequester = remember { FocusRequester() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
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
                    text = stringResource(id = Strings.reset_password),
                    style = MaterialTheme.typography.headlineSmall,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(id = Strings.password_reset_description),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                )
                Spacer(modifier = Modifier.height(24.dp))
                EmailField(
                    value = uiState.email,
                    onValueChange = {
                        onEvent(RecoverUiEvent.OnEmailValueChange(it))
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                    placeholder = {
                        Text(text = stringResource(id = Strings.email))
                    },
                    isError = uiState.emailError != null,
                    errorMessage = uiState.emailError?.asString().orEmpty(),
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        onEvent(RecoverUiEvent.Recover)
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                    shape = MaterialTheme.shapes.large,
                ) {
                    Text(text = stringResource(id = Strings.recover))
                }
            }
        },
    )

    ProgressDialog(
        text = stringResource(id = Strings.sending_recovery_email),
        openDialog = uiState.openProgressDialog,
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Preview
@Composable
private fun RecoverScreenPreview() {
    EdumateTheme(dynamicColor = false) {
        RecoverScreenContent(
            uiState = RecoverUiState(),
            onEvent = {},
            snackbarHostState = SnackbarHostState(),
        )
    }
}
