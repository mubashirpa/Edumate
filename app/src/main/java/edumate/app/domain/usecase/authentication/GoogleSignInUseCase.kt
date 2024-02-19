package edumate.app.domain.usecase.authentication

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.onesignal.OneSignal
import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.domain.repository.FirebaseAuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import edumate.app.R.string as Strings

class GoogleSignInUseCase
    @Inject
    constructor(
        private val repository: FirebaseAuthRepository,
    ) {
        operator fun invoke(idToken: String): Flow<Result<FirebaseUser?>> =
            flow {
                try {
                    emit(Result.Loading())
                    val user = repository.signInWithGoogle(idToken)
                    if (user != null) {
                        val userEmail = user.email
                        val userPhoneNumber = user.phoneNumber
                        OneSignal.login(user.uid)
                        if (userEmail != null) {
                            OneSignal.User.addEmail(userEmail)
                        }
                        if (userPhoneNumber != null) {
                            OneSignal.User.addSms(userPhoneNumber)
                        }
                    }
                    emit(Result.Success(user))
                } catch (e: FirebaseAuthInvalidUserException) {
                    if (e.errorCode == "ERROR_USER_DISABLED") {
                        emit(Result.Error(UiText.StringResource(Strings.auth_error_user_disabled)))
                    } else {
                        emit(Result.Error(UiText.StringResource(Strings.auth_invalid_user_exception)))
                    }
                } catch (e: FirebaseAuthInvalidCredentialsException) {
                    emit(Result.Error(UiText.StringResource(Strings.auth_invalid_credentials_exception)))
                } catch (e: FirebaseAuthUserCollisionException) {
                    emit(Result.Error(UiText.StringResource(Strings.auth_error_email_already_in_use)))
                } catch (e: FirebaseNetworkException) {
                    emit(Result.Error(UiText.StringResource(Strings.auth_network_exception)))
                } catch (e: Exception) {
                    emit(Result.Error(UiText.StringResource(Strings.auth_unknown_exception)))
                }
            }
    }
