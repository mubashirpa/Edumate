package edumate.app.presentation.login.screen

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.flowWithLifecycle
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import edumate.app.R.drawable as Drawables
import edumate.app.R.string as Strings
import edumate.app.presentation.components.EdumateSnackbarHost
import edumate.app.presentation.components.EmailField
import edumate.app.presentation.components.PasswordField
import edumate.app.presentation.components.ProgressDialog
import edumate.app.presentation.login.LoginUiEvent
import edumate.app.presentation.login.LoginViewModel
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalLayoutApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    navigateToRegister: () -> Unit,
    navigateToRecover: (email: String) -> Unit,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val currentOnLoginSuccess by rememberUpdatedState(onLoginSuccess)
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarScope = rememberCoroutineScope()
    val oneTapClient = remember {
        Identity.getSignInClient(context)
    }
    val signInRequest = remember {
        BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(context.getString(Strings.default_web_client_id))
                    .setFilterByAuthorizedAccounts(true)
                    .build()
            ).build()
    }
    val activityResultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                    val idToken = credential.googleIdToken
                    if (idToken != null) {
                        viewModel.onEvent(LoginUiEvent.OnGoogleSignInClick(idToken))
                    } else {
                        snackbarScope.launch {
                            snackbarHostState.showSnackbar(
                                context.getString(Strings.error_auth_google_no_token)
                            )
                        }
                    }
                } catch (e: ApiException) {
                    when (e.statusCode) {
                        CommonStatusCodes.NETWORK_ERROR -> {
                            snackbarScope.launch {
                                snackbarHostState.showSnackbar(
                                    context.getString(Strings.error_auth_google_network_error)
                                )
                            }
                        }

                        else -> {
                            snackbarScope.launch {
                                snackbarHostState.showSnackbar(
                                    context.getString(Strings.error_auth_google_api_exception)
                                )
                            }
                        }
                    }
                }
            }
        }

    LaunchedEffect(viewModel, lifecycle) {
        // Whenever the uiState changes, check if the userProfile is logged in and
        // call the `onLoginSuccess` event when `lifecycle` is at least STARTED
        snapshotFlow { viewModel.uiState }.filter { it.isUserLoggedIn }.flowWithLifecycle(lifecycle)
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

    Scaffold(
        snackbarHost = {
            EdumateSnackbarHost(snackbarHostState)
        },
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
                        text = stringResource(id = Strings.welcome_back),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(id = Strings.please_sign_in_to_your_account),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    EmailField(
                        value = viewModel.uiState.email,
                        onValueChange = {
                            viewModel.onEvent(LoginUiEvent.EmailChanged(it.trim()))
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
                            viewModel.onEvent(LoginUiEvent.PasswordChanged(it.trim()))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(text = stringResource(id = Strings.password))
                        },
                        isError = viewModel.uiState.passwordError != null,
                        errorMessage = viewModel.uiState.passwordError?.asString()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(id = Strings.forgot_password),
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable(onClick = { navigateToRecover(viewModel.uiState.email) }),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    Button(
                        onClick = {
                            viewModel.onEvent(LoginUiEvent.OnSignInClick)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Text(text = stringResource(id = Strings.sign_in))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = {
                            oneTapClient.beginSignIn(signInRequest)
                                .addOnSuccessListener { result ->
                                    val request =
                                        IntentSenderRequest.Builder(
                                            result.pendingIntent.intentSender
                                        )
                                            .build()
                                    try {
                                        activityResultLauncher.launch(request)
                                    } catch (_: ActivityNotFoundException) {
                                        snackbarScope.launch {
                                            snackbarHostState.showSnackbar(
                                                context.getString(
                                                    Strings.error_auth_google_activity_not_found
                                                )
                                            )
                                        }
                                    }
                                }
                                .addOnFailureListener {
                                    snackbarScope.launch {
                                        snackbarHostState.showSnackbar(
                                            context.getString(Strings.error_auth_google_failed)
                                        )
                                    }
                                }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Icon(
                            painter = painterResource(id = Drawables.ic_google),
                            contentDescription = stringResource(id = Strings.google),
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                            tint = Color.Unspecified
                        )
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Text(text = stringResource(id = Strings.sign_in_with_google))
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Row {
                        Text(text = stringResource(id = Strings.dont_have_an_account))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = Strings.sign_up),
                            modifier = Modifier.clickable(onClick = navigateToRegister),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    )

    ProgressDialog(
        text = stringResource(id = Strings.signing_in),
        openDialog = viewModel.uiState.openProgressDialog
    )
}