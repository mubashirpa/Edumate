package edumate.app.presentation.enrolled

import edumate.app.domain.model.Course

data class EnrolledUiState(
    val loading: Boolean = false,
    val success: Boolean = false,
    val classes: List<Course> = emptyList()
)