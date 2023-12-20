package edumate.app.domain.usecase.authentication

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.onesignal.OneSignal
import edumate.app.R.string as Strings
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.domain.repository.FirebaseAuthRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GoogleSignInUseCase @Inject constructor(
    private val repository: FirebaseAuthRepository
) {
    operator fun invoke(idToken: String): Flow<Resource<FirebaseUser?>> = flow {
        try {
            emit(Resource.Loading())
            val user = repository.signInWithGoogle(idToken)
            if (user != null) {
                OneSignal.login(user.uid)
                if (user.email != null) {
                    OneSignal.User.addEmail(user.email!!)
                }
            }
            emit(Resource.Success(user))
        } catch (e: FirebaseAuthInvalidUserException) {
            if (e.errorCode == "ERROR_USER_DISABLED") {
                emit(Resource.Error(UiText.StringResource(Strings.auth_error_user_disabled)))
            } else {
                emit(Resource.Error(UiText.StringResource(Strings.auth_invalid_user_exception)))
            }
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            emit(Resource.Error(UiText.StringResource(Strings.auth_invalid_credentials_exception)))
        } catch (e: FirebaseAuthUserCollisionException) {
            emit(Resource.Error(UiText.StringResource(Strings.auth_error_email_already_in_use)))
        } catch (e: FirebaseNetworkException) {
            emit(Resource.Error(UiText.StringResource(Strings.auth_network_exception)))
        } catch (e: Exception) {
            emit(Resource.Error(UiText.StringResource(Strings.auth_unknown_exception)))
        }
    }
}