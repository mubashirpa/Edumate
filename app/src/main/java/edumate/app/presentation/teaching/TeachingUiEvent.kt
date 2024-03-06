package edumate.app.presentation.teaching

import edumate.app.domain.model.classroom.courses.Course

sealed class TeachingUiEvent {
    data class DeleteCourse(val courseId: String?) : TeachingUiEvent()

    data class LeaveCourse(val courseId: String?) : TeachingUiEvent()

    data class OnOpenDeleteCourseDialogChange(val course: Course?) : TeachingUiEvent()

    data class OnOpenLeaveCourseDialogChange(val course: Course?) : TeachingUiEvent()

    data object Refresh : TeachingUiEvent()

    data object UserMessageShown : TeachingUiEvent()
}
