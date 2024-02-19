package edumate.app.domain.usecase.authentication

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.domain.repository.FirebaseAuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import edumate.app.R.string as Strings

class SendPasswordResetEmailUseCase
    @Inject
    constructor(
        private val repository: FirebaseAuthRepository,
    ) {
        operator fun invoke(email: String): Flow<Result<String>> =
            flow {
                try {
                    emit(Result.Loading())
                    val result = repository.sendPasswordResetEmail(email)
                    emit(Result.Success(result))
                } catch (e: FirebaseAuthInvalidUserException) {
                    if (e.errorCode == "ERROR_USER_NOT_FOUND") {
                        emit(
                            Result.Error(
                                UiText.StringResource(
                                    Strings.auth_error_user_not_found,
                                    email,
                                ),
                            ),
                        )
                    } else {
                        emit(Result.Error(UiText.StringResource(Strings.auth_invalid_user_exception)))
                    }
                } catch (e: FirebaseNetworkException) {
                    emit(Result.Error(UiText.StringResource(Strings.auth_network_exception)))
                } catch (e: Exception) {
                    emit(Result.Error(UiText.StringResource(Strings.auth_unknown_exception)))
                }
            }
    }
