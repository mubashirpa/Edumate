package app.edumate.domain.usecase.authentication

import app.edumate.domain.repository.AuthenticationRepository

class IsUserLoggedInUseCase(
    private val authenticationRepository: AuthenticationRepository,
) {
    suspend operator fun invoke(): Boolean = authenticationRepository.isUserLoggedIn()
}
