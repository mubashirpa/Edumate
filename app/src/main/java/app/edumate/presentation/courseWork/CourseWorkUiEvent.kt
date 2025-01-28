package app.edumate.presentation.courseWork

import app.edumate.domain.model.courseWork.CourseWork

sealed class CourseWorkUiEvent {
    data class OnDeleteCourseWork(
        val id: String,
    ) : CourseWorkUiEvent()

    data class OnExpandedAppBarDropdownChange(
        val expanded: Boolean,
    ) : CourseWorkUiEvent()

    data class OnOpenDeleteCourseWorkDialogChange(
        val courseWork: CourseWork?,
    ) : CourseWorkUiEvent()

    data class OnShowCreateCourseWorkBottomSheetChange(
        val show: Boolean,
    ) : CourseWorkUiEvent()

    data object OnRefresh : CourseWorkUiEvent()

    data object OnRetry : CourseWorkUiEvent()

    data object UserMessageShown : CourseWorkUiEvent()
}
