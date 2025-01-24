package app.edumate.domain.usecase.authentication

import app.edumate.R
import app.edumate.core.Result
import app.edumate.core.UiText
import app.edumate.domain.repository.AuthenticationRepository
import io.github.jan.supabase.auth.exception.AuthErrorCode
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.exceptions.HttpRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SignInUseCase(
    private val authenticationRepository: AuthenticationRepository,
) {
    operator fun invoke(
        email: String,
        password: String,
        remember: Boolean,
        onEmailNotConfirmed: () -> Unit,
    ): Flow<Result<UserInfo>> =
        flow {
            try {
                emit(Result.Loading())
                if (remember) {
                    authenticationRepository.saveSignInInfo(email, password)
                } else {
                    authenticationRepository.clearSignInInfo()
                }
                val user = authenticationRepository.signInWithEmail(email, password)
                emit(Result.Success(user!!))
            } catch (e: AuthRestException) {
                e.printStackTrace()
                when (e.errorCode) {
                    AuthErrorCode.UserNotFound -> {
                        emit(Result.Error(UiText.StringResource(R.string.auth_error_user_not_found)))
                    }

                    AuthErrorCode.UserBanned -> {
                        emit(Result.Error(UiText.StringResource(R.string.auth_error_user_disabled)))
                    }

                    AuthErrorCode.EmailNotConfirmed -> {
                        emit(Result.Error(UiText.StringResource(R.string.auth_error_email_not_confirmed)))
                        onEmailNotConfirmed()
                    }

                    AuthErrorCode.InvalidCredentials -> {
                        emit(Result.Error(UiText.StringResource(R.string.auth_error_invalid_credentials)))
                    }

                    else -> {
                        emit(Result.Error(UiText.DynamicString(e.message.toString())))
                    }
                }
            } catch (_: HttpRequestTimeoutException) {
                emit(Result.Error(UiText.StringResource(R.string.auth_timeout_exception)))
            } catch (_: HttpRequestException) {
                emit(Result.Error(UiText.StringResource(R.string.auth_network_exception)))
            } catch (_: Exception) {
                emit(Result.Error(UiText.StringResource(R.string.auth_unknown_exception)))
            }
        }
}
