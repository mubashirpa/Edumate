package app.edumate.domain.usecase.preferences

import app.edumate.domain.model.preferences.UserPreferences
import app.edumate.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow

class GetUserPreferencesUseCase(
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    operator fun invoke(): Flow<UserPreferences> = userPreferencesRepository.userPreferencesFlow
}
