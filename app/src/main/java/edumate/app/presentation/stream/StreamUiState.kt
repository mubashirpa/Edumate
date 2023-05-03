package edumate.app.presentation.stream

import edumate.app.core.DataState
import edumate.app.core.UiText
import edumate.app.domain.model.announcements.Announcement

data class StreamUiState(
    val appBarMenuExpanded: Boolean = false,
    val announcements: List<Announcement> = emptyList(),
    val dataState: DataState = DataState.UNKNOWN,
    val refreshing: Boolean = false,
    val userMessage: UiText? = null
)