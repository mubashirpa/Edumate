package app.edumate.domain.usecase.authentication

import app.edumate.domain.model.preferences.LoginPreferences
import app.edumate.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow

class GetLoginPreferencesUseCase(
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    operator fun invoke(): Flow<LoginPreferences> = userPreferencesRepository.loginPreferencesFlow
}
