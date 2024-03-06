package edumate.app.domain.usecase.authentication

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.onesignal.OneSignal
import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.data.mapper.toUserProfileDomainModel
import edumate.app.domain.model.userProfiles.UserProfile
import edumate.app.domain.repository.AuthenticationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import edumate.app.R.string as Strings

class SignInUseCase
    @Inject
    constructor(
        private val repository: AuthenticationRepository,
    ) {
        operator fun invoke(
            email: String,
            password: String,
        ): Flow<Result<UserProfile>> =
            flow {
                try {
                    emit(Result.Loading())
                    val user =
                        repository.signInWithEmailAndPassword(email, password)
                            .toUserProfileDomainModel()
                    user.id?.let { userId ->
                        OneSignal.login(userId)
                        user.emailAddress?.let { email ->
                            OneSignal.User.addEmail(email)
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
