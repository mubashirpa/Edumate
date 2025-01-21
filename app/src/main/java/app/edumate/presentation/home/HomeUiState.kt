package app.edumate.presentation.home

import androidx.compose.foundation.text.input.TextFieldState
import app.edumate.core.Result
import app.edumate.core.UiText
import app.edumate.domain.model.User
import app.edumate.domain.model.courses.Courses

data class HomeUiState(
    val coursesResult: Result<List<Courses>> = Result.Loading(),
    val currentUser: User? = null,
    val expandedAppBarDropdown: Boolean = false,
    val isRefreshing: Boolean = false,
    val joinCourseUiState: JoinCourseBottomSheetUiState = JoinCourseBottomSheetUiState(),
    val showAddCourseBottomSheet: Boolean = false,
    val userMessage: UiText? = null,
)

data class JoinCourseBottomSheetUiState(
    val courseId: TextFieldState = TextFieldState(),
    val courseIdError: UiText? = null,
    val showBottomSheet: Boolean = false,
)
