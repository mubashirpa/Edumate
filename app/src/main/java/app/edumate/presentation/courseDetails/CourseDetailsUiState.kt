package app.edumate.presentation.courseDetails

import app.edumate.core.Result
import app.edumate.domain.model.courses.Course

data class CourseDetailsUiState(
    val courseResult: Result<Course> = Result.Empty(),
)
