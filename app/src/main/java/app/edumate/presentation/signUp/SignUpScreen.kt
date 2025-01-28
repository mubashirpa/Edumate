package app.edumate.presentation.signUp

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import app.edumate.R
import app.edumate.presentation.components.EmailField
import app.edumate.presentation.components.GoogleSignInButton
import app.edumate.presentation.components.NameField
import app.edumate.presentation.components.PasswordField
import app.edumate.presentation.components.ProgressDialog
import app.edumate.presentation.theme.EdumateTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SignUpScreen(
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    onNavigateToSignIn: () -> Unit,
    onSignUpComplete: (isVerified: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = koinViewModel(),
) {
    val currentOnSignUpComplete by rememberUpdatedState(onSignUpComplete)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val context = LocalContext.current

    LaunchedEffect(viewModel, lifecycle) {
        snapshotFlow { viewModel.uiState }
            .filter { it.userLoginStatus.first }
            .flowWithLifecycle(lifecycle)
            .collect {
                if (!it.userLoginStatus.second) {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(context.getString(R.string.success_signup_with_email))
                    }
                }
                currentOnSignUpComplete(it.userLoginStatus.second)
            }
    }

    SignUpContent(
        uiState = viewModel.uiState,
        onEvent = viewModel::onEvent,
        onNavigateToSignIn = onNavigateToSignIn,
        modifier = modifier,
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SignUpContent(
    uiState: SignUpUiState,
    onEvent: (SignUpUiEvent) -> Unit,
    onNavigateToSignIn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    uiState.userMessage?.let { userMessage ->
        LaunchedEffect(userMessage) {
            snackbarHostState.showSnackbar(userMessage.asString(context))
            // Once the message is displayed and dismissed, notify the ViewModel.
            onEvent(SignUpUiEvent.UserMessageShown)
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
                    text = stringResource(id = R.string.create_new_account),
                    style = MaterialTheme.typography.headlineSmall,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(id = R.string.please_fill_in_the_form_to_continue),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )
                Spacer(modifier = Modifier.height(24.dp))
                NameField(
                    state = uiState.name,
                    focusManager = focusManager,
                    keyboardController = keyboardController,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(text = stringResource(id = R.string.full_name))
                    },
                    isError = uiState.nameError != null,
                    errorMessage = uiState.nameError?.asString(),
                    imeAction = ImeAction.Next,
                )
                Spacer(modifier = Modifier.height(12.dp))
                EmailField(
                    state = uiState.email,
                    focusManager = focusManager,
                    keyboardController = keyboardController,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(text = stringResource(id = R.string.email))
                    },
                    isError = uiState.emailError != null,
                    errorMessage = uiState.emailError?.asString(),
                    imeAction = ImeAction.Next,
                )
                Spacer(modifier = Modifier.height(12.dp))
                PasswordField(
                    state = uiState.password,
                    focusManager = focusManager,
                    keyboardController = keyboardController,
                    modifier = Modifier.fillMaxWidth(),
                    autofillType = listOf(AutofillType.NewPassword),
                    placeholder = {
                        Text(text = stringResource(id = R.string.password))
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
                        Text(text = stringResource(id = R.string.confirm_password))
                    },
                    isError = uiState.repeatedPasswordError != null,
                    errorMessage = uiState.repeatedPasswordError?.asString(),
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        onEvent(SignUpUiEvent.SignUp)
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                    shape = MaterialTheme.shapes.large,
                ) {
                    Text(
                        text = stringResource(id = R.string.sign_up),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                GoogleSignInButton(
                    filterByAuthorizedAccounts = false,
                    onSignInSuccess = { token, nonce ->
                        onEvent(SignUpUiEvent.SignUpWithGoogle(token, nonce))
                    },
                    onSignInFailure = { message ->
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(message)
                        }
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                    textStyle = MaterialTheme.typography.bodyLarge,
                )
                Spacer(modifier = Modifier.height(24.dp))
                SignInText(onClick = onNavigateToSignIn)
            }
        },
    )

    ProgressDialog(
        text = stringResource(R.string.creating_account),
        open = uiState.openProgressDialog,
        onDismissRequest = {},
    )
}

@Composable
private fun SignInText(onClick: () -> Unit) {
    val text =
        buildAnnotatedString {
            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onBackground)) {
                append(stringResource(id = R.string.have_an_account).plus(" "))
            }
            withLink(
                LinkAnnotation.Clickable(
                    tag = "SignIn",
                    styles = TextLinkStyles(style = SpanStyle(color = MaterialTheme.colorScheme.primary)),
                    linkInteractionListener = {
                        onClick()
                    },
                ),
            ) {
                append(stringResource(id = R.string.sign_in))
            }
        }

    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
    )
}

@Preview
@Composable
private fun SignUpScreenPreview() {
    EdumateTheme {
        SignUpContent(
            uiState = SignUpUiState(),
            onEvent = {},
            onNavigateToSignIn = {},
        )
    }
}
