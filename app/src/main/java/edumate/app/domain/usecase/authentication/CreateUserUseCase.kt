package edumate.app.domain.usecase.authentication

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.onesignal.OneSignal
import edumate.app.R.string as Strings
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.domain.repository.FirebaseAuthRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CreateUserUseCase @Inject constructor(
    private val repository: FirebaseAuthRepository
) {
    operator fun invoke(
        name: String,
        email: String,
        password: String
    ): Flow<Resource<FirebaseUser?>> = flow {
        try {
            emit(Resource.Loading())
            val user = repository.createUserWithEmailAndPassword(name, email, password)
            if (user != null) {
                OneSignal.setExternalUserId(user.uid)
                if (user.email != null) {
                    OneSignal.setEmail(user.email!!)
                }
            }
            emit(Resource.Success(user))
        } catch (e: FirebaseAuthException) {
            when (e.errorCode) {
                "ERROR_EMAIL_ALREADY_IN_USE" -> {
                    emit(
                        Resource.Error(
                            UiText.StringResource(Strings.auth_error_email_already_in_use)
                        )
                    )
                }

                else -> {
                    emit(Resource.Error(UiText.StringResource(Strings.auth_unknown_exception)))
                }
            }
        } catch (e: FirebaseNetworkException) {
            emit(Resource.Error(UiText.StringResource(Strings.auth_network_exception)))
        } catch (e: Exception) {
            emit(Resource.Error(UiText.StringResource(Strings.auth_unknown_exception)))
        }
    }
}