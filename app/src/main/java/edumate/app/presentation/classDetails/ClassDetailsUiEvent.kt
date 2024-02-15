package edumate.app.presentation.classDetails

import edumate.app.domain.model.classroom.courseWork.CourseWork
import edumate.app.domain.model.classroom.students.Student

sealed class ClassDetailsUiEvent {
    data class OnNavigateToViewStudentWork(
        val courseWork: CourseWork,
        val assignedStudent: Student,
    ) : ClassDetailsUiEvent()

    data object OnRetry : ClassDetailsUiEvent()
}
