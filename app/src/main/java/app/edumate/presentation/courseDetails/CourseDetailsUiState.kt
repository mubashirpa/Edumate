package app.edumate.presentation.courseDetails

import app.edumate.core.Result
import app.edumate.domain.model.courses.CourseWithMembers

data class CourseDetailsUiState(
    val courseResult: Result<CourseWithMembers> = Result.Empty(),
    val currentUserRole: CurrentUserRole = CurrentUserRole.STUDENT,
)
