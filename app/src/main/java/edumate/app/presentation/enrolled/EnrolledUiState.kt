package edumate.app.presentation.enrolled

import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.domain.model.courses.Course

data class EnrolledUiState(
    val enrolledCoursesResource: Resource<List<Course>> = Resource.Unknown(),
    val openProgressDialog: Boolean = false,
    val refreshing: Boolean = false,
    val unEnrolCourseId: String? = null,
    val userMessage: UiText? = null,
)
