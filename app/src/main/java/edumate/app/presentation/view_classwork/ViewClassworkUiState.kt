package edumate.app.presentation.view_classwork

import edumate.app.core.UiText
import edumate.app.domain.model.course_work.CourseWork

data class ViewClassworkUiState(
    val classwork: CourseWork = CourseWork(),
    val error: UiText? = null,
    val userMessage: UiText? = null
)