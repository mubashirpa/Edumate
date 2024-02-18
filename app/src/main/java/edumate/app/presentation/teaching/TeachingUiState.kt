package edumate.app.presentation.teaching

import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.domain.model.classroom.courses.Course

data class TeachingUiState(
    val deleteCourseId: String? = null,
    val openProgressDialog: Boolean = false,
    val refreshing: Boolean = false,
    val teachingCoursesResult: Result<List<Course>> = Result.Empty(),
    val userMessage: UiText? = null,
)
