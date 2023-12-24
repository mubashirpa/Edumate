package edumate.app.presentation.enrolled

import edumate.app.core.UiText
import edumate.app.core.utils.ResourceNew
import edumate.app.domain.model.courses.Course

data class EnrolledUiState(
    val enrolledCoursesResource: ResourceNew<List<Course>> = ResourceNew.Unknown(),
    val openProgressDialog: Boolean = false,
    val refreshing: Boolean = false,
    val unEnrolCourseId: String? = null,
    val userMessage: UiText? = null,
)
