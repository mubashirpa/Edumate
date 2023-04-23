package edumate.app.presentation.class_details

import edumate.app.core.DataState
import edumate.app.domain.model.courses.Course

data class ClassDetailsUiState(
    val course: Course? = null,
    val dataState: DataState = DataState.UNKNOWN
)