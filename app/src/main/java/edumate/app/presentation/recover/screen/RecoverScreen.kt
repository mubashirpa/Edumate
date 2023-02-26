package edumate.app.presentation.recover.screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.flowWithLifecycle
import edumate.app.R.string as Strings
import edumate.app.presentation.components.EdumateSnackbarHost
import edumate.app.presentation.components.EmailField
import edumate.app.presentation.components.ProgressDialog
import edumate.app.presentation.recover.RecoverUiEvent
import edumate.app.presentation.recover.RecoverViewModel
import kotlinx.coroutines.flow.filter

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun RecoverScreen(
    viewModel: RecoverViewModel = hiltViewModel(),
    onPasswordResetEmailSent: () -> Unit
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
                Toast.makeText(
                    context,
                    context.getString(
                        Strings.success_send_password_reset_email,
                        viewModel.uiState.email
                    ),
                    Toast.LENGTH_LONG
                ).show()
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

    Scaffold(
        snackbarHost = { EdumateSnackbarHost(snackbarHostState) },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .consumeWindowInsets(innerPadding)
                    .padding(innerPadding),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(30.dp))
                    // Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = stringResource(id = Strings.reset_password),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(id = Strings.password_reset_description),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    // Spacer(modifier = Modifier.weight(0.5f))
                    EmailField(
                        value = viewModel.uiState.email,
                        onValueChange = {
                            viewModel.onEvent(RecoverUiEvent.EmailChanged(it.trim()))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(text = stringResource(id = Strings.email))
                        },
                        isError = viewModel.uiState.emailError != null,
                        errorMessage = viewModel.uiState.emailError?.asString()
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    // Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = {
                            viewModel.onEvent(RecoverUiEvent.OnRecoverClick)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Text(text = stringResource(id = Strings.recover))
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                    // Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    )

    ProgressDialog(
        text = stringResource(id = Strings.sending_recovery_email),
        openDialog = viewModel.uiState.openProgressDialog
    )
}