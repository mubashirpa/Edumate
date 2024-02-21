package edumate.app.presentation.teaching

import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.domain.model.classroom.courses.Course

data class TeachingUiState(
    val deleteCourseId: String? = null,
    val isRefreshing: Boolean = false,
    val openProgressDialog: Boolean = false,
    val teachingCoursesResult: Result<List<Course>> = Result.Empty(),
    val userId: String? = null,
    val userMessage: UiText? = null,
)
