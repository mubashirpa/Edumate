package app.edumate.domain.usecase.authentication

import app.edumate.R
import app.edumate.core.Result
import app.edumate.core.UiText
import app.edumate.domain.repository.AuthenticationRepository
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.exceptions.HttpRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UpdatePasswordUseCase(
    private val authenticationRepository: AuthenticationRepository,
) {
    operator fun invoke(newPassword: String): Flow<Result<UserInfo>> =
        flow {
            try {
                emit(Result.Loading())
                val user = authenticationRepository.updatePassword(newPassword)
                emit(Result.Success(user))
            } catch (e: AuthRestException) {
                emit(Result.Error(UiText.DynamicString(e.message.toString())))
            } catch (_: HttpRequestTimeoutException) {
                emit(Result.Error(UiText.StringResource(R.string.auth_timeout_exception)))
            } catch (_: HttpRequestException) {
                emit(Result.Error(UiText.StringResource(R.string.auth_network_exception)))
            } catch (_: Exception) {
                emit(Result.Error(UiText.StringResource(R.string.auth_unknown_exception)))
            }
        }
}
