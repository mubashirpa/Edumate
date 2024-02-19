package edumate.app.presentation.login

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.flowWithLifecycle
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import edumate.app.core.Constants
import edumate.app.presentation.components.EmailField
import edumate.app.presentation.components.PasswordField
import edumate.app.presentation.components.ProgressDialog
import edumate.app.presentation.ui.theme.EdumateTheme
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import edumate.app.R.drawable as Drawables
import edumate.app.R.string as Strings

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    navigateToRegister: () -> Unit,
    navigateToRecover: (email: String) -> Unit,
    onLoginSuccess: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val currentOnLoginSuccess by rememberUpdatedState(onLoginSuccess)
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel, lifecycle) {
        // Whenever the uiState changes, check if the userProfile is logged in and
        // call the `onLoginSuccess` event when `lifecycle` is at least STARTED
        snapshotFlow { viewModel.uiState }
            .filter { it.isUserLoggedIn }
            .flowWithLifecycle(lifecycle)
            .collect {
                currentOnLoginSuccess()
            }
    }

    viewModel.uiState.userMessage?.let { userMessage ->
        LaunchedEffect(userMessage) {
            snackbarHostState.showSnackbar(userMessage.asString(context))
            // Once the message is displayed and dismissed, notify the ViewModel.
            viewModel.onEvent(LoginUiEvent.UserMessageShown)
        }
    }

    LoginScreenContent(
        uiState = viewModel.uiState,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState,
        navigateToRegister = navigateToRegister,
        navigateToRecover = navigateToRecover,
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun LoginScreenContent(
    uiState: LoginUiState,
    onEvent: (LoginUiEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    navigateToRegister: () -> Unit,
    navigateToRecover: (email: String) -> Unit,
) {
    val context = LocalContext.current
    val snackbarScope = rememberCoroutineScope()
    val oneTapClient = remember { Identity.getSignInClient(context) }
    val signInRequest =
        remember {
            BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(
                    BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(Constants.GOOGLE_SERVER_CLIENT_ID)
                        .setFilterByAuthorizedAccounts(true)
                        .build(),
                ).build()
        }
    val activityResultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                    val idToken = credential.googleIdToken
                    if (idToken != null) {
                        onEvent(LoginUiEvent.SignInWithGoogle(idToken))
                    } else {
                        snackbarScope.launch {
                            snackbarHostState.showSnackbar(
                                context.getString(Strings.error_auth_google_no_token),
                            )
                        }
                    }
                } catch (e: ApiException) {
                    when (e.statusCode) {
                        CommonStatusCodes.NETWORK_ERROR -> {
                            snackbarScope.launch {
                                snackbarHostState.showSnackbar(
                                    context.getString(Strings.error_auth_google_network_error),
                                )
                            }
                        }

                        else -> {
                            snackbarScope.launch {
                                snackbarHostState.showSnackbar(
                                    context.getString(Strings.error_auth_google_api_exception),
                                )
                            }
                        }
                    }
                }
            }
        }
    val noAccountText = stringResource(id = Strings.dont_have_an_account).plus(" ")
    val signUpText = stringResource(id = Strings.sign_up)
    val noAccountAnnotatedText =
        buildAnnotatedString {
            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onBackground)) {
                append(noAccountText)
            }
            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                append(signUpText)
                addStringAnnotation(
                    tag = "SignUp",
                    annotation = "SignUp",
                    start = noAccountText.length,
                    end = noAccountText.length + signUpText.length,
                )
            }
        }

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
                    text = stringResource(id = Strings.welcome_back),
                    style = MaterialTheme.typography.headlineSmall,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(id = Strings.please_sign_in_to_your_account),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )
                Spacer(modifier = Modifier.height(24.dp))
                EmailField(
                    value = uiState.email,
                    onValueChange = {
                        onEvent(LoginUiEvent.OnEmailValueChange(it))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(text = stringResource(id = Strings.email))
                    },
                    isError = uiState.emailError != null,
                    errorMessage = uiState.emailError?.asString().orEmpty(),
                    imeAction = ImeAction.Next,
                )
                Spacer(modifier = Modifier.height(12.dp))
                PasswordField(
                    value = uiState.password,
                    onValueChange = {
                        onEvent(LoginUiEvent.OnPasswordValueChange(it))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(text = stringResource(id = Strings.password))
                    },
                    isError = uiState.passwordError != null,
                    errorMessage = uiState.passwordError?.asString().orEmpty(),
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(id = Strings.forgot_password),
                    modifier =
                        Modifier
                            .align(Alignment.End)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    navigateToRecover(uiState.email)
                                },
                            ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        onEvent(LoginUiEvent.SignIn)
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                    shape = MaterialTheme.shapes.large,
                ) {
                    Text(text = stringResource(id = Strings.sign_in))
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = {
                        oneTapClient.beginSignIn(signInRequest)
                            .addOnSuccessListener { result ->
                                val request =
                                    IntentSenderRequest.Builder(
                                        result.pendingIntent.intentSender,
                                    )
                                        .build()
                                try {
                                    activityResultLauncher.launch(request)
                                } catch (_: ActivityNotFoundException) {
                                    snackbarScope.launch {
                                        snackbarHostState.showSnackbar(
                                            context.getString(
                                                Strings.error_auth_google_activity_not_found,
                                            ),
                                        )
                                    }
                                }
                            }
                            .addOnFailureListener {
                                snackbarScope.launch {
                                    snackbarHostState.showSnackbar(
                                        context.getString(Strings.error_auth_google_failed),
                                    )
                                }
                            }
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                    shape = MaterialTheme.shapes.large,
                ) {
                    Icon(
                        painter = painterResource(id = Drawables.ic_google),
                        contentDescription = stringResource(id = Strings.google),
                        tint = Color.Unspecified,
                    )
                    Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = stringResource(id = Strings.sign_in_with_google))
                }
                Spacer(modifier = Modifier.height(24.dp))
                ClickableText(
                    text = noAccountAnnotatedText,
                    onClick = { offset ->
                        noAccountAnnotatedText.getStringAnnotations(
                            tag = "SignUp",
                            start = offset,
                            end = offset + signUpText.length,
                        ).firstOrNull()?.let {
                            navigateToRegister()
                        }
                    },
                )
            }
        },
    )

    ProgressDialog(
        text = stringResource(id = Strings.signing_in),
        openDialog = uiState.openProgressDialog,
    )
}

@Preview
@Composable
private fun LoginScreenPreview() {
    EdumateTheme(dynamicColor = false) {
        LoginScreenContent(
            uiState = LoginUiState(),
            onEvent = {},
            snackbarHostState = SnackbarHostState(),
            navigateToRegister = {},
            navigateToRecover = {},
        )
    }
}
