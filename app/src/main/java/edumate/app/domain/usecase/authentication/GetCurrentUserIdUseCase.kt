package edumate.app.domain.usecase.authentication

import edumate.app.domain.repository.AuthenticationRepository
import javax.inject.Inject

class GetCurrentUserIdUseCase
    @Inject
    constructor(
        private val repository: AuthenticationRepository,
    ) {
        operator fun invoke() = repository.currentUserId
    }
