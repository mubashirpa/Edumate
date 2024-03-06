package edumate.app.domain.usecase.authentication

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.domain.repository.AuthenticationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import edumate.app.R.string as Strings

class SendPasswordResetEmailUseCase
    @Inject
    constructor(
        private val repository: AuthenticationRepository,
    ) {
        operator fun invoke(email: String): Flow<Result<String>> =
            flow {
                try {
                    emit(Result.Loading())
                    val result = repository.sendPasswordResetEmail(email)
                    emit(Result.Success(result))
                } catch (e: FirebaseAuthInvalidUserException) {
                    emit(Result.Error(UiText.StringResource(Strings.auth_error_user_not_found, email)))
                } catch (e: FirebaseNetworkException) {
                    emit(Result.Error(UiText.StringResource(Strings.auth_network_exception)))
                } catch (e: Exception) {
                    emit(Result.Error(UiText.StringResource(Strings.auth_unknown_exception)))
                }
            }
    }
