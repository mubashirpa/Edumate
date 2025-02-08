package app.edumate.domain.usecase.preferences

import app.edumate.domain.model.preferences.AppTheme
import app.edumate.domain.repository.UserPreferencesRepository

class ConfigureAppThemeUseCase(
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    suspend operator fun invoke(appTheme: AppTheme) {
        userPreferencesRepository.configureAppTheme(appTheme)
    }
}
