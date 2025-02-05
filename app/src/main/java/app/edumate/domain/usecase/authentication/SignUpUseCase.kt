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

class SignUpUseCase(
    private val authenticationRepository: AuthenticationRepository,
) {
    operator fun invoke(
        name: String,
        email: String,
        password: String,
    ): Flow<Result<UserInfo>> =
        flow {
            try {
                emit(Result.Loading())
                authenticationRepository.signUpWithEmail(name, email, password)?.let { user ->
                    if (user.identities.isNullOrEmpty()) {
                        emit(Result.Error(UiText.StringResource(R.string.auth_error_email_already_in_use)))
                    } else {
                        authenticationRepository.saveSignInInfo(email, password)
                        emit(Result.Success(user))
                    }
                } ?: emit(Result.Error(UiText.StringResource(R.string.auth_unknown_exception)))
            } catch (e: AuthRestException) {
                when (e.errorCode) {
                    AuthErrorCode.WeakPassword -> {
                        emit(Result.Error(UiText.StringResource(R.string.auth_error_weak_password)))
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
