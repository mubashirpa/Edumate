package edumate.app.presentation.classwork

import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.domain.model.classroom.courseWork.CourseWork

data class ClassworkUiState(
    val appBarDropdownExpanded: Boolean = false,
    val courseWorkResult: Result<List<CourseWork>> = Result.Empty(),
    val deleteCourseWork: CourseWork? = null,
    val openProgressDialog: Boolean = false,
    val isRefreshing: Boolean = false,
    val showCreateCourseWorkBottomSheet: Boolean = false,
    val userId: String? = null,
    val userMessage: UiText? = null,
)
