package edumate.app.domain.usecase.authentication

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
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

class CreateUserUseCase
    @Inject
    constructor(
        private val repository: AuthenticationRepository,
    ) {
        operator fun invoke(
            name: String,
            email: String,
            password: String,
        ): Flow<Result<UserProfile>> =
            flow {
                try {
                    emit(Result.Loading())
                    val user =
                        repository.createUserWithEmailAndPassword(name, email, password)
                            .toUserProfileDomainModel()
                    user.id?.let { userId ->
                        OneSignal.login(userId)
                        user.emailAddress?.let { email ->
                            OneSignal.User.addEmail(email)
                        }
                    }
                    emit(Result.Success(user))
                } catch (e: FirebaseAuthUserCollisionException) {
                    emit(Result.Error(UiText.StringResource(Strings.auth_error_email_already_in_use)))
                } catch (e: FirebaseNetworkException) {
                    emit(Result.Error(UiText.StringResource(Strings.auth_network_exception)))
                } catch (e: Exception) {
                    emit(Result.Error(UiText.StringResource(Strings.auth_unknown_exception)))
                }
            }
    }
