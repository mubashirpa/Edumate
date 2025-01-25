package app.edumate.presentation.home

import androidx.compose.foundation.text.input.TextFieldState
import app.edumate.core.Result
import app.edumate.core.UiText
import app.edumate.domain.model.User
import app.edumate.domain.model.courses.Courses

data class HomeUiState(
    val coursesResult: Result<List<Courses>> = Result.Empty(),
    val currentUser: User? = null,
    val enrolledCourses: List<Courses> = emptyList(),
    val expandedAppBarDropdown: Boolean = false,
    val isRefreshing: Boolean = false,
    val joinCourseUiState: JoinCourseBottomSheetUiState = JoinCourseBottomSheetUiState(),
    val openProgressDialog: Boolean = false,
    val showAddCourseBottomSheet: Boolean = false,
    val teachingCourses: List<Courses> = emptyList(),
    val userMessage: UiText? = null,
)

data class JoinCourseBottomSheetUiState(
    val courseId: TextFieldState = TextFieldState(),
    val courseIdError: UiText? = null,
    val error: UiText? = null,
    val showBottomSheet: Boolean = false,
)
