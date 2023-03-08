package edumate.app.presentation.teaching

import edumate.app.core.UiText
import edumate.app.domain.model.Course

data class TeachingUiState(
    val classes: List<Course> = emptyList(),
    val error: UiText? = null,
    val loading: Boolean = false,
    val success: Boolean = false
)