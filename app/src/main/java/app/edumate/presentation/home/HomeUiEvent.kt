package app.edumate.presentation.home

import app.edumate.domain.model.course.Course

sealed class HomeUiEvent {
    data class DeleteCourse(
        val courseId: String,
    ) : HomeUiEvent()

    data class JoinCourse(
        val courseId: String,
    ) : HomeUiEvent()

    data class LeaveCourse(
        val courseId: String,
    ) : HomeUiEvent()

    data class OnAppBarDropdownExpandedChange(
        val expanded: Boolean,
    ) : HomeUiEvent()

    data class OnOpenDeleteCourseDialogChange(
        val courseId: String?,
    ) : HomeUiEvent()

    data class OnOpenLeaveCourseDialogChange(
        val course: Course?,
    ) : HomeUiEvent()

    data class OnOpenUnenrollDialogChange(
        val courseId: String?,
    ) : HomeUiEvent()

    data class OnShowAddCourseBottomSheetChange(
        val show: Boolean,
    ) : HomeUiEvent()

    data class OnShowJoinCourseBottomSheetChange(
        val show: Boolean,
    ) : HomeUiEvent()

    data class UnenrollCourse(
        val courseId: String,
    ) : HomeUiEvent()

    data object OnRefresh : HomeUiEvent()

    data object OnRetry : HomeUiEvent()

    data object UserMessageShown : HomeUiEvent()
}
