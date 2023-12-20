package edumate.app.presentation.register.screen

import android.app.Activity
import android.content.ActivityNotFoundException
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.flowWithLifecycle
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import edumate.app.presentation.components.EdumateSnackbarHost
import edumate.app.presentation.components.EmailField
import edumate.app.presentation.components.NameField
import edumate.app.presentation.components.PasswordField
import edumate.app.presentation.components.ProgressDialog
import edumate.app.presentation.register.RegisterUiEvent
import edumate.app.presentation.register.RegisterViewModel
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import edumate.app.R.drawable as Drawables
import edumate.app.R.string as Strings

@OptIn(
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
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            ).build()
    }
    val activityResultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                    val idToken = credential.googleIdToken
                    if (idToken != null) {
                        viewModel.onEvent(RegisterUiEvent.OnGoogleSignUpClick(idToken))
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
                        Text(text = stringResource(id = Strings.sign_up_with_google))
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