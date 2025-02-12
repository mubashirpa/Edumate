package app.edumate.presentation.courseWork

import app.edumate.domain.model.courseWork.CourseWork

sealed class CourseWorkUiEvent {
    data class DeleteCourseWork(
        val courseWorkId: String,
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

    data object Refresh : CourseWorkUiEvent()

    data object Retry : CourseWorkUiEvent()

    data object UserMessageShown : CourseWorkUiEvent()
}
