package edumate.app.presentation.teaching

import edumate.app.domain.model.Course

data class TeachingUiState(
    val loading: Boolean = false,
    val success: Boolean = false,
    val classes: List<Course> = emptyList()
)