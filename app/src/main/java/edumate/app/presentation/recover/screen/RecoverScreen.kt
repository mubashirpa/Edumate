package edumate.app.presentation.recover.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.flowWithLifecycle
import edumate.app.presentation.components.EdumateSnackbarHost
import edumate.app.presentation.components.EmailField
import edumate.app.presentation.components.ProgressDialog
import edumate.app.presentation.recover.RecoverUiEvent
import edumate.app.presentation.recover.RecoverViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import edumate.app.R.string as Strings

@OptIn(
    ExperimentalComposeUiApi::class,
)
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
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        try {
            focusRequester.requestFocus()
        } catch (_: Exception) {
        }
    }

    LaunchedEffect(viewModel, lifecycle) {
        // Whenever the uiState changes, check if the password reset email sent and
        // call the `onRecoverSuccess` event when `lifecycle` is at least STARTED
        snapshotFlow { viewModel.uiState }
            .filter { it.isPasswordResetEmailSend }
            .flowWithLifecycle(lifecycle)
            .collect {
                currentOnPasswordResetEmailSent()
                rootSnackbarScope.launch {
                    rootSnackbarHostState.showSnackbar(
                        context.getString(
                            Strings.success_send_password_reset_email,
                            viewModel.uiState.email,
                        ),
                    )
                }
            }
    }

    viewModel.uiState.userMessage?.let { userMessage ->
        LaunchedEffect(userMessage) {
            snackbarHostState.showSnackbar(userMessage.asString(context))
            // Once the message is displayed and dismissed, notify the ViewModel.
            viewModel.onEvent(RecoverUiEvent.UserMessageShown)
        }
    }

    Scaffold(
        snackbarHost = {
            EdumateSnackbarHost(snackbarHostState)
        },
        content = { innerPadding ->
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .consumeWindowInsets(innerPadding)
                        .padding(innerPadding),
                contentAlignment = Alignment.TopCenter,
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth(0.9f)
                            .fillMaxHeight()
                            .imePadding()
                            .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(modifier = Modifier.height(30.dp))
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
                        value = viewModel.uiState.email,
                        onValueChange = {
                            viewModel.onEvent(RecoverUiEvent.EmailChanged(it.trim()))
                        },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester),
                        placeholder = {
                            Text(text = stringResource(id = Strings.email))
                        },
                        isError = viewModel.uiState.emailError != null,
                        errorMessage = viewModel.uiState.emailError?.asString().orEmpty(),
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    Button(
                        onClick = {
                            viewModel.onEvent(RecoverUiEvent.OnRecoverClick)
                        },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                        shape = MaterialTheme.shapes.large,
                    ) {
                        Text(text = stringResource(id = Strings.recover))
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        },
    )

    ProgressDialog(
        text = stringResource(id = Strings.sending_recovery_email),
        openDialog = viewModel.uiState.openProgressDialog,
    )
}
