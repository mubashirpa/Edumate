package app.edumate.presentation.deepLinkHandler

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import app.edumate.R
import app.edumate.core.Result
import app.edumate.core.UiText
import app.edumate.presentation.MainActivity
import app.edumate.presentation.components.ErrorScreen
import app.edumate.presentation.components.LoadingScreen
import app.edumate.presentation.newPassword.NewPasswordScreen
import app.edumate.presentation.theme.EdumateTheme
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.handleDeeplinks
import org.koin.android.ext.android.inject

class DeepLinkHandlerActivity : ComponentActivity() {
    private val client: SupabaseClient by inject()
    private var uiState by mutableStateOf(DeepLinkHandlerUiState())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            client.handleDeeplinks(
                intent = intent,
                onSessionSuccess = { userSession ->
                    uiState = uiState.copy(sessionResult = Result.Success(userSession))
                },
            )
        } catch (e: Exception) {
            uiState =
                uiState.copy(sessionResult = Result.Error(UiText.DynamicString(e.message.toString())))
        }

        setContent {
            EdumateTheme {
                Scaffold { innerPadding ->
                    when (val sessionResult = uiState.sessionResult) {
                        is Result.Empty -> {}

                        is Result.Error -> {
                            ErrorScreen(
                                onRetryClick = {
                                    navigateToMainApp()
                                },
                                modifier =
                                    Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                errorMessage = sessionResult.message!!.asString(),
                                buttonText = stringResource(R.string.exit),
                            )
                        }

                        is Result.Loading -> {
                            LoadingScreen(modifier = Modifier.padding(innerPadding))
                        }

                        is Result.Success -> {
                            val session = sessionResult.data!!
                            when (session.type) {
                                "recovery" -> {
                                    val email = session.user?.email.orEmpty()

                                    NewPasswordScreen(
                                        email = email,
                                        onUpdatePasswordComplete = {
                                            navigateToMainApp()
                                        },
                                        modifier = Modifier.fillMaxSize().padding(innerPadding),
                                    )
                                }

                                else -> {
                                    LaunchedEffect(true) {
                                        navigateToMainApp()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun navigateToMainApp() {
        val intent =
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        startActivity(intent)
    }
}
