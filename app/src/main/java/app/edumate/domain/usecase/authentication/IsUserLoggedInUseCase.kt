package app.edumate.domain.usecase.authentication

import app.edumate.domain.repository.AuthenticationRepository
import kotlinx.coroutines.flow.Flow

class IsUserLoggedInUseCase(
    private val authenticationRepository: AuthenticationRepository,
) {
    operator fun invoke(): Flow<Boolean> = authenticationRepository.isLoggedIn
}
