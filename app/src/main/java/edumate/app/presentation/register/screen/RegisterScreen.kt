package edumate.app.presentation.register.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.flowWithLifecycle
import edumate.app.R.string as Strings
import edumate.app.presentation.components.*
import edumate.app.presentation.register.RegisterUiEvent
import edumate.app.presentation.register.RegisterViewModel
import kotlinx.coroutines.flow.filter

@OptIn(
    ExperimentalLayoutApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    navigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val currentOnRegisterSuccess by rememberUpdatedState(onRegisterSuccess)
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel, lifecycle) {
        // Whenever the uiState changes, check if the user is logged in and
        // call the `onRegisterSuccess` event when `lifecycle` is at least STARTED
        snapshotFlow { viewModel.uiState }.filter { it.isUserLoggedIn }.flowWithLifecycle(lifecycle)
            .collect {
                currentOnRegisterSuccess()
            }
    }

    viewModel.uiState.userMessage?.let { userMessage ->
        LaunchedEffect(userMessage) {
            snackbarHostState.showSnackbar(userMessage.asString(context))
            // Once the message is displayed and dismissed, notify the ViewModel.
            viewModel.onEvent(RegisterUiEvent.UserMessageShown)
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
                        .imePadding()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(30.dp))
                    Text(
                        text = stringResource(id = Strings.create_new_account),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(id = Strings.please_fill_in_the_form_to_continue),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    NameField(
                        value = viewModel.uiState.name,
                        onValueChange = {
                            viewModel.onEvent(RegisterUiEvent.NameChanged(it))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(text = stringResource(id = Strings.full_name))
                        },
                        isError = viewModel.uiState.nameError != null,
                        errorMessage = viewModel.uiState.nameError?.asString(),
                        imeAction = ImeAction.Next
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    EmailField(
                        value = viewModel.uiState.email,
                        onValueChange = {
                            viewModel.onEvent(RegisterUiEvent.EmailChanged(it.trim()))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(text = stringResource(id = Strings.email))
                        },
                        isError = viewModel.uiState.emailError != null,
                        errorMessage = viewModel.uiState.emailError?.asString(),
                        imeAction = ImeAction.Next
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    PasswordField(
                        value = viewModel.uiState.password,
                        onValueChange = {
                            viewModel.onEvent(RegisterUiEvent.PasswordChanged(it.trim()))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(text = stringResource(id = Strings.password))
                        },
                        isError = viewModel.uiState.passwordError != null,
                        errorMessage = viewModel.uiState.passwordError?.asString(),
                        autofillTypes = listOf(AutofillType.NewPassword)
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    Button(
                        onClick = {
                            viewModel.onEvent(RegisterUiEvent.OnSignUpClick)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Text(text = stringResource(id = Strings.sign_up))
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Row {
                        Text(text = stringResource(id = Strings.have_an_account))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = Strings.sign_in),
                            modifier = Modifier.clickable(onClick = navigateToLogin),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    )

    ProgressDialog(
        text = stringResource(id = Strings.creating_account),
        openDialog = viewModel.uiState.openProgressDialog
    )
}