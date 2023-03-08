package edumate.app.presentation.enrolled

import edumate.app.core.UiText
import edumate.app.domain.model.Course

data class EnrolledUiState(
    val classes: List<Course> = emptyList(),
    val error: UiText? = null,
    val loading: Boolean = false,
    val openProgressDialog: Boolean = false,
    val success: Boolean = false,
    val userMessage: UiText? = null
)