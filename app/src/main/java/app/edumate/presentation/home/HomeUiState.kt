package app.edumate.presentation.home

import app.edumate.core.Result
import app.edumate.core.UiText
import app.edumate.domain.model.course.Course
import app.edumate.domain.model.user.User

data class HomeUiState(
    val coursesResult: Result<List<Course>> = Result.Empty(),
    val currentUser: User? = null,
    val deleteCourseId: String? = null,
    val enrolledCourses: List<Course> = emptyList(),
    val expandedAppBarDropdown: Boolean = false,
    val isRefreshing: Boolean = false,
    val leaveCourse: Course? = null,
    val openProgressDialog: Boolean = false,
    val showAddCourseBottomSheet: Boolean = false,
    val showJoinCourseBottomSheet: Boolean = false,
    val teachingCourses: List<Course> = emptyList(),
    val unenrollCourseId: String? = null,
    val userMessage: UiText? = null,
)
