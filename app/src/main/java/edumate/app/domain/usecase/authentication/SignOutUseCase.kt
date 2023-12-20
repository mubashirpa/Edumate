package edumate.app.domain.usecase.authentication

import com.onesignal.OneSignal
import edumate.app.domain.repository.FirebaseAuthRepository
import javax.inject.Inject

class SignOutUseCase
    @Inject
    constructor(
        private val repository: FirebaseAuthRepository,
    ) {
        operator fun invoke() {
            repository.signOut()
            OneSignal.logout()
        }
    }
