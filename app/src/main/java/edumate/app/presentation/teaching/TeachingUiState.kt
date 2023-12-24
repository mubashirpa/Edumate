package edumate.app.presentation.teaching

import edumate.app.core.UiText
import edumate.app.core.utils.ResourceNew
import edumate.app.domain.model.courses.Course

data class TeachingUiState(
    val deleteCourseId: String? = null,
    val openProgressDialog: Boolean = false,
    val refreshing: Boolean = false,
    val teachingCoursesResource: ResourceNew<List<Course>> = ResourceNew.Unknown(),
    val userMessage: UiText? = null,
)
