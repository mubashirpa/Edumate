package app.edumate.presentation.components

import android.content.Context
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import app.edumate.R
import app.edumate.core.Constants
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID

@Composable
fun GoogleSignInButton(
    filterByAuthorizedAccounts: Boolean,
    onSignInSuccess: (idToken: String, nonce: String) -> Unit,
    onSignInFailure: (message: String) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.large,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge,
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val onClick: () -> Unit = {
        val credentialManager = CredentialManager.create(context)

        // Generate a nonce and hash it with sha-256
        // Providing a nonce is optional but recommended
        val rawNonce =
            UUID
                .randomUUID()
                .toString() // Generate a random String. UUID should be sufficient, but can also be any other random string.
        val bytes = rawNonce.toString().toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        val hashedNonce =
            digest.fold("") { str, it -> str + "%02x".format(it) } // Hashed nonce to be passed to Google sign-in

        val googleIdOption: GetGoogleIdOption =
            GetGoogleIdOption
                .Builder()
                .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts)
                .setServerClientId(Constants.WEB_GOOGLE_CLIENT_ID)
                .setNonce(hashedNonce) // Provide the nonce if you have one
                .build()

        val request: GetCredentialRequest =
            GetCredentialRequest
                .Builder()
                .addCredentialOption(googleIdOption)
                .build()

        coroutineScope.launch {
            try {
                val result =
                    credentialManager.getCredential(
                        request = request,
                        context = context,
                    )

                handleSignIn(
                    context = context,
                    result = result,
                    onSignInSuccess = { googleIdToken ->
                        onSignInSuccess(googleIdToken, rawNonce)
                    },
                    onSignInFailure = onSignInFailure,
                )
            } catch (_: GetCredentialException) {
                onSignInFailure(context.getString(R.string.error_auth_google_credential_exception))
            } catch (e: NoCredentialException) {
                onSignInFailure(e.message.toString())
            } catch (e: Exception) {
                onSignInFailure(e.message.toString())
            }
        }
    }

    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = shape,
    ) {
        val text =
            if (filterByAuthorizedAccounts) {
                stringResource(id = R.string.sign_in_with_google)
            } else {
                stringResource(id = R.string.sign_up_with_google)
            }

        Icon(
            painter = painterResource(id = R.drawable.ic_google),
            contentDescription = stringResource(id = R.string.google),
            tint = Color.Unspecified,
        )
        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
        Text(text = text, style = textStyle)
    }
}

private fun handleSignIn(
    context: Context,
    result: GetCredentialResponse,
    onSignInSuccess: (idToken: String) -> Unit,
    onSignInFailure: (message: String) -> Unit,
) {
    when (val credential = result.credential) {
        is CustomCredential -> {
            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                try {
                    val googleIdTokenCredential =
                        GoogleIdTokenCredential
                            .createFrom(credential.data)

                    val googleIdToken = googleIdTokenCredential.idToken

                    onSignInSuccess(googleIdToken)
                } catch (_: GoogleIdTokenParsingException) {
                    onSignInFailure(context.getString(R.string.error_auth_google_invalid_id_token))
                }
            } else {
                onSignInFailure(context.getString(R.string.error_auth_google_unexpected_credential))
            }
        }

        else -> {
            onSignInFailure(context.getString(R.string.error_auth_google_unexpected_credential))
        }
    }
}
