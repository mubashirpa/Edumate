package edumate.app.presentation.classwork

import edumate.app.core.UiText
import edumate.app.domain.model.course_work.CourseWork

data class ClassworkUiState(
    val dataState: DataState = DataState.UNKNOWN,
    val errorMessage: UiText = UiText.Empty,
    val classWorks: List<CourseWork> = listOf(),
    val openFabMenu: Boolean = false
)