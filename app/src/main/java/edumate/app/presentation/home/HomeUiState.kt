package edumate.app.presentation.home

import edumate.app.core.UiText
import edumate.app.domain.model.Course

data class HomeUiState(
    val loading: Boolean = false,
    val success: Boolean = false,
    val rooms: List<Course> = emptyList(),
    val error: Boolean = false,
    val errorMessage: UiText = UiText.Empty,
    val openFabMenu: Boolean = false
)