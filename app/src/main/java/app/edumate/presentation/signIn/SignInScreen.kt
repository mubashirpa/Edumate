package app.edumate.presentation.signIn

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
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
import androidx.compose.ui.autofill.ContentType
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
import app.edumate.presentation.components.PasswordField
import app.edumate.presentation.components.ProgressDialog
import app.edumate.presentation.signIn.components.VerifyEmailBottomSheet
import app.edumate.presentation.theme.EdumateTheme
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun SignInScreen(
    onNavigateToSignUp: () -> Unit,
    onNavigateToForgetPassword: (email: String?) -> Unit,
    onSignInComplete: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignInViewModel = koinViewModel(),
) {
    val currentOnSignInComplete by rememberUpdatedState(onSignInComplete)
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(viewModel, lifecycle) {
        snapshotFlow { viewModel.uiState }
            .filter { it.isUserLoggedIn }
            .flowWithLifecycle(lifecycle)
            .collect {
                currentOnSignInComplete()
            }
    }

    SignInContent(
        uiState = viewModel.uiState,
        onEvent = viewModel::onEvent,
        onNavigateToSignUp = onNavigateToSignUp,
        onNavigateToForgetPassword = onNavigateToForgetPassword,
        modifier = modifier,
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SignInContent(
    uiState: SignInUiState,
    onEvent: (SignInUiEvent) -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToForgetPassword: (email: String?) -> Unit,
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
            onEvent(SignInUiEvent.UserMessageShown)
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
                    text = stringResource(id = R.string.welcome_back),
                    style = MaterialTheme.typography.headlineSmall,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(id = R.string.please_sign_in_to_your_account),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )
                Spacer(modifier = Modifier.height(24.dp))
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
                    fieldContentType = ContentType.Password,
                    placeholder = {
                        Text(text = stringResource(id = R.string.password))
                    },
                    isError = uiState.passwordError != null,
                    errorMessage = uiState.passwordError?.asString(),
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Switch(
                        checked = uiState.rememberPassword,
                        onCheckedChange = {
                            onEvent(SignInUiEvent.OnRememberSwitchCheckedChange(it))
                        },
                    )
                    Text(
                        text = stringResource(R.string.remember),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text =
                            buildAnnotatedString {
                                withLink(
                                    LinkAnnotation.Clickable(
                                        tag = "ForgotPassword",
                                        styles = TextLinkStyles(style = SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)),
                                        linkInteractionListener = {
                                            onNavigateToForgetPassword(
                                                uiState.email.text
                                                    .toString()
                                                    .ifBlank { null },
                                            )
                                        },
                                    ),
                                ) {
                                    append(stringResource(id = R.string.forgot_password))
                                }
                            },
                        modifier = Modifier.minimumInteractiveComponentSize(),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        onEvent(SignInUiEvent.SignIn)
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                    shape = MaterialTheme.shapes.large,
                ) {
                    Text(
                        text = stringResource(id = R.string.sign_in),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                GoogleSignInButton(
                    filterByAuthorizedAccounts = false,
                    onSignInSuccess = { token, nonce ->
                        onEvent(SignInUiEvent.SignInWithGoogle(token, nonce))
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
                SignUpText(onClick = onNavigateToSignUp)
            }
        },
    )

    ProgressDialog(
        text = stringResource(R.string.signing_in),
        open = uiState.openProgressDialog,
        onDismissRequest = {},
    )

    VerifyEmailBottomSheet(
        show = uiState.showVerifyEmailBottomSheet,
        onDismissRequest = {
            onEvent(SignInUiEvent.OnShowVerifyEmailBottomSheetChange(false))
        },
        onResendVerifyEmail = {
            onEvent(SignInUiEvent.ResendVerifyEmail)
        },
    )
}

@Composable
private fun SignUpText(onClick: () -> Unit) {
    val text =
        buildAnnotatedString {
            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onBackground)) {
                append(stringResource(id = R.string.dont_have_an_account).plus(" "))
            }
            withLink(
                LinkAnnotation.Clickable(
                    tag = "SignUp",
                    styles = TextLinkStyles(style = SpanStyle(color = MaterialTheme.colorScheme.primary)),
                    linkInteractionListener = {
                        onClick()
                    },
                ),
            ) {
                append(stringResource(id = R.string.sign_up))
            }
        }

    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
    )
}

@Preview
@Composable
private fun SignInScreenPreview() {
    EdumateTheme {
        SignInContent(
            uiState = SignInUiState(),
            onEvent = {},
            onNavigateToSignUp = {},
            onNavigateToForgetPassword = {},
        )
    }
}
