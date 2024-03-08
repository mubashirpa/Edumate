package edumate.app.presentation.classDetails

import edumate.app.core.Result
import edumate.app.domain.model.classroom.courses.Course

data class ClassDetailsUiState(
    val courseResult: Result<Course> = Result.Empty(),
)
