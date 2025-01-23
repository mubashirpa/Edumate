package app.edumate.presentation.deepLinkHandler

import app.edumate.core.Result
import io.github.jan.supabase.auth.user.UserSession

data class DeepLinkHandlerUiState(
    val sessionResult: Result<UserSession> = Result.Loading(),
)
