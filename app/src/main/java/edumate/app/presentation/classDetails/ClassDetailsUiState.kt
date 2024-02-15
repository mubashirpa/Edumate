package edumate.app.presentation.classDetails

import edumate.app.core.Result
import edumate.app.domain.model.classroom.courseWork.CourseWork
import edumate.app.domain.model.classroom.courses.Course
import edumate.app.domain.model.classroom.students.Student

data class ClassDetailsUiState(
    val course: Course? = null,
    val courseResult: Result<Course> = Result.Empty(),
    val courseWork: CourseWork? = null,
    val courseWorkAssignedStudent: Student? = null,
)
