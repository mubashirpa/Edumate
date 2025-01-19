package app.edumate.domain.usecase.authentication

import app.edumate.domain.model.LoginPreferences
import app.edumate.domain.repository.AuthenticationRepository
import kotlinx.coroutines.flow.Flow

class GetSignInInfoUseCase(
    private val authenticationRepository: AuthenticationRepository,
) {
    operator fun invoke(): Flow<LoginPreferences> = authenticationRepository.signInInfo
}
