package app.edumate.presentation.courseDetails

import app.edumate.core.Result
import app.edumate.domain.model.course.CourseWithMembers

data class CourseDetailsUiState(
    val courseResult: Result<CourseWithMembers> = Result.Empty(),
    val currentUserRole: CourseUserRole = CourseUserRole.Student,
)
