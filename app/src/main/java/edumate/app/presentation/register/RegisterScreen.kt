package edumate.app.presentation.register

import android.app.Activity
import android.content.ActivityNotFoundException
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import edumate.app.presentation.components.NameField
import edumate.app.presentation.components.PasswordField
import edumate.app.presentation.components.ProgressDialog
import edumate.app.presentation.ui.theme.EdumateTheme
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import edumate.app.R.drawable as Drawables
import edumate.app.R.string as Strings

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    navigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val currentOnRegisterSuccess by rememberUpdatedState(onRegisterSuccess)
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel, lifecycle) {
        // Whenever the uiState changes, check if the userProfile is logged in and
        // call the `onRegisterSuccess` event when `lifecycle` is at least STARTED
        snapshotFlow { viewModel.uiState }
            .filter { it.isUserLoggedIn }
            .flowWithLifecycle(lifecycle)
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

    RegisterScreenContent(
        uiState = viewModel.uiState,
        onEvent = viewModel::onEvent,
        snackbarHostState = snackbarHostState,
        navigateToLogin = navigateToLogin,
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun RegisterScreenContent(
    uiState: RegisterUiState,
    onEvent: (RegisterUiEvent) -> Unit,
    snackbarHostState: SnackbarHostState,
    navigateToLogin: () -> Unit,
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
                        .setFilterByAuthorizedAccounts(false)
                        .build(),
                ).build()
        }
    val activityResultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                    val idToken = credential.googleIdToken
                    if (idToken != null) {
                        onEvent(RegisterUiEvent.SignInWithGoogle(idToken))
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
    val haveAccountText = stringResource(id = Strings.have_an_account).plus(" ")
    val signInText = stringResource(id = Strings.sign_in)
    val haveAccountAnnotatedText =
        buildAnnotatedString {
            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onBackground)) {
                append(haveAccountText)
            }
            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                append(signInText)
                addStringAnnotation(
                    tag = "SignIn",
                    annotation = "SignIn",
                    start = haveAccountText.length,
                    end = haveAccountText.length + signInText.length,
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
                    text = stringResource(id = Strings.create_new_account),
                    style = MaterialTheme.typography.headlineSmall,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(id = Strings.please_fill_in_the_form_to_continue),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )
                Spacer(modifier = Modifier.height(24.dp))
                NameField(
                    value = uiState.name,
                    onValueChange = {
                        onEvent(RegisterUiEvent.OnNameValueChange(it))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(text = stringResource(id = Strings.full_name))
                    },
                    isError = uiState.nameError != null,
                    errorMessage = uiState.nameError?.asString().orEmpty(),
                    imeAction = ImeAction.Next,
                )
                Spacer(modifier = Modifier.height(12.dp))
                EmailField(
                    value = uiState.email,
                    onValueChange = {
                        onEvent(RegisterUiEvent.OnEmailValueChange(it))
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
                        onEvent(RegisterUiEvent.OnPasswordValueChange(it))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(text = stringResource(id = Strings.password))
                    },
                    isError = uiState.passwordError != null,
                    errorMessage = uiState.passwordError?.asString().orEmpty(),
                    autofillTypes = listOf(AutofillType.NewPassword),
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        onEvent(RegisterUiEvent.SignUp)
                    },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                    shape = MaterialTheme.shapes.large,
                ) {
                    Text(text = stringResource(id = Strings.sign_up))
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
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(text = stringResource(id = Strings.sign_up_with_google))
                }
                Spacer(modifier = Modifier.height(24.dp))
                ClickableText(
                    text = haveAccountAnnotatedText,
                    onClick = { offset ->
                        haveAccountAnnotatedText.getStringAnnotations(
                            tag = "SignIn",
                            start = offset,
                            end = offset + signInText.length,
                        ).firstOrNull()?.let {
                            navigateToLogin()
                        }
                    },
                )
            }
        },
    )

    ProgressDialog(
        text = stringResource(id = Strings.creating_account),
        openDialog = uiState.openProgressDialog,
    )
}

@Preview
@Composable
private fun RegisterScreenPreview() {
    EdumateTheme(dynamicColor = false) {
        RegisterScreenContent(
            uiState = RegisterUiState(),
            onEvent = {},
            snackbarHostState = SnackbarHostState(),
            navigateToLogin = {},
        )
    }
}
