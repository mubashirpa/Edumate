package edumate.app.presentation.home

import edumate.app.core.UiText
import edumate.app.domain.model.Room

data class HomeUiState(
    val loading: Boolean = false,
    val success: Boolean = false,
    val rooms: List<Room> = emptyList(),
    val error: Boolean = false,
    val errorMessage: UiText = UiText.Empty
)