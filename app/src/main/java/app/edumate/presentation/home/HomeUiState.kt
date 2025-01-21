package app.edumate.presentation.home

import app.edumate.core.Result
import app.edumate.domain.model.User
import app.edumate.domain.model.courses.Courses

data class HomeUiState(
    val coursesResult: Result<List<Courses>> = Result.Empty(),
    val currentUser: User? = null,
    val expandedAppBarDropdown: Boolean = false,
    val isRefreshing: Boolean = false,
    val showAddCourseBottomSheet: Boolean = false,
    val showCreateCourseBottomSheet: Boolean = false,
    val showJoinCourseBottomSheet: Boolean = false,
)
