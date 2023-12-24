package edumate.app.presentation.teaching

import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.domain.model.courses.Course

data class TeachingUiState(
    val deleteCourseId: String? = null,
    val openProgressDialog: Boolean = false,
    val refreshing: Boolean = false,
    val teachingCoursesResource: Resource<List<Course>> = Resource.Unknown(),
    val userMessage: UiText? = null,
)
