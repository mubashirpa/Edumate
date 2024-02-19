package edumate.app.domain.usecase.authentication

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.onesignal.OneSignal
import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.domain.repository.FirebaseAuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import edumate.app.R.string as Strings

class SignInUseCase
    @Inject
    constructor(
        private val repository: FirebaseAuthRepository,
    ) {
        operator fun invoke(
            email: String,
            password: String,
        ): Flow<Result<FirebaseUser?>> =
            flow {
                try {
                    emit(Result.Loading())
                    val user = repository.signInWithEmailAndPassword(email, password)
                    if (user != null) {
                        val userEmail = user.email
                        OneSignal.login(user.uid)
                        if (userEmail != null) {
                            OneSignal.User.addEmail(userEmail)
                        }
                    }
                    emit(Result.Success(user))
                } catch (e: FirebaseAuthException) {
                    when (e.errorCode) {
                        "ERROR_WRONG_PASSWORD" -> {
                            emit(Result.Error(UiText.StringResource(Strings.auth_error_wrong_password)))
                        }

                        "ERROR_USER_NOT_FOUND" -> {
                            emit(
                                Result.Error(
                                    UiText.StringResource(
                                        Strings.auth_error_user_not_found,
                                        email,
                                    ),
                                ),
                            )
                        }

                        "ERROR_USER_DISABLED" -> {
                            emit(Result.Error(UiText.StringResource(Strings.auth_error_user_disabled)))
                        }

                        "ERROR_TOO_MANY_REQUESTS" -> {
                            emit(Result.Error(UiText.StringResource(Strings.auth_error_too_many_requests)))
                        }

                        else -> {
                            emit(Result.Error(UiText.StringResource(Strings.auth_unknown_exception)))
                        }
                    }
                } catch (e: FirebaseNetworkException) {
                    emit(Result.Error(UiText.StringResource(Strings.auth_network_exception)))
                } catch (e: Exception) {
                    emit(Result.Error(UiText.StringResource(Strings.auth_unknown_exception)))
                }
            }
    }
