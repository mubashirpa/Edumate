package edumate.app.presentation.teaching

import edumate.app.core.DataState
import edumate.app.core.UiText
import edumate.app.domain.model.courses.Course

data class TeachingUiState(
    val courses: List<Course> = emptyList(),
    val dataState: DataState = DataState.UNKNOWN,
    val deleteCourseId: String? = null,
    val openProgressDialog: Boolean = false,
    val refreshing: Boolean = false,
    val userMessage: UiText? = null
)