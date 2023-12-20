package edumate.app.presentation.class_details

import edumate.app.domain.model.course_work.CourseWork
import edumate.app.domain.model.user_profiles.UserProfile

sealed class ClassDetailsUiEvent {
    data class OnNavigateToViewStudentWork(
        val courseWork: CourseWork,
        val assignedStudent: UserProfile,
    ) :
        ClassDetailsUiEvent()

    data object OnRetry : ClassDetailsUiEvent()
}
