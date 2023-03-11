package edumate.app.presentation.classwork

import edumate.app.data.remote.dto.CourseWorkDto

data class ClassworkUiState(
    val classWorks: List<CourseWorkDto> = listOf(),
    val openFabMenu: Boolean = false
)