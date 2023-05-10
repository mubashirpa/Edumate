package edumate.app.presentation.enrolled

import edumate.app.core.DataState
import edumate.app.core.UiText
import edumate.app.domain.model.courses.Course

data class EnrolledUiState(
    val courses: List<Course> = emptyList(),
    val dataState: DataState = DataState.UNKNOWN,
    val openProgressDialog: Boolean = false,
    val refreshing: Boolean = false,
    val unEnrolCourseId: String? = null,
    val userMessage: UiText? = null
)