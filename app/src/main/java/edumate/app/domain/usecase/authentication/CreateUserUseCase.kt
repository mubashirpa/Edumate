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

class CreateUserUseCase
    @Inject
    constructor(
        private val repository: FirebaseAuthRepository,
    ) {
        operator fun invoke(
            name: String,
            email: String,
            password: String,
        ): Flow<Result<FirebaseUser?>> =
            flow {
                try {
                    emit(Result.Loading())
                    val user = repository.createUserWithEmailAndPassword(name, email, password)
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
                } catch (e: FirebaseAuthException) {
                    when (e.errorCode) {
                        "ERROR_EMAIL_ALREADY_IN_USE" -> {
                            emit(Result.Error(UiText.StringResource(Strings.auth_error_email_already_in_use)))
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
