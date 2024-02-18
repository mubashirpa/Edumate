package edumate.app.presentation.enrolled

import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.domain.model.classroom.courses.Course

data class EnrolledUiState(
    val enrolledCoursesResult: Result<List<Course>> = Result.Empty(),
    val isRefreshing: Boolean = false,
    val openProgressDialog: Boolean = false,
    val unEnrolCourseId: String? = null,
    val userId: String? = null,
    val userMessage: UiText? = null,
)
