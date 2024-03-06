package edumate.app.domain.usecase.authentication

import com.onesignal.OneSignal
import edumate.app.domain.repository.AuthenticationRepository
import javax.inject.Inject

class SignOutUseCase
    @Inject
    constructor(
        private val repository: AuthenticationRepository,
    ) {
        operator fun invoke() {
            repository.signOut()
            OneSignal.logout()
        }
    }
