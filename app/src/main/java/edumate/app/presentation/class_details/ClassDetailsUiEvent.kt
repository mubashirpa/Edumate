package edumate.app.presentation.class_details

import edumate.app.domain.model.User
import edumate.app.domain.model.course_work.CourseWork

sealed class ClassDetailsUiEvent {
    data class OnNavigateToViewStudentWork(val courseWork: CourseWork, val assignedStudent: User) :
        ClassDetailsUiEvent()

    object OnRetry : ClassDetailsUiEvent()
}
