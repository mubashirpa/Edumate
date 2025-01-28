package app.edumate.presentation.courseWork

import app.edumate.core.Result
import app.edumate.core.UiText
import app.edumate.domain.model.courseWork.CourseWork

data class CourseWorkUiState(
    val courseWorkResult: Result<List<CourseWork>> = Result.Empty(),
    val deleteCourseWork: CourseWork? = null,
    val expandedAppBarDropdown: Boolean = false,
    val isRefreshing: Boolean = false,
    val openProgressDialog: Boolean = false,
    val showCreateCourseWorkBottomSheet: Boolean = false,
    val userMessage: UiText? = null,
)
