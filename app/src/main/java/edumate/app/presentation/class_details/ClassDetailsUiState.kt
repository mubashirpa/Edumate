package edumate.app.presentation.class_details

import edumate.app.core.DataState
import edumate.app.domain.model.course_work.CourseWork
import edumate.app.domain.model.courses.Course
import edumate.app.domain.model.user_profiles.UserProfile

data class ClassDetailsUiState(
    val course: Course? = null,
    val dataState: DataState = DataState.UNKNOWN,
    val courseWork: CourseWork? = null,
    val courseWorkAssignedStudent: UserProfile? = null
)