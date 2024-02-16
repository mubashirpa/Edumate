package edumate.app.presentation.classwork

import edumate.app.domain.model.classroom.courseWork.CourseWork

sealed class ClassworkUiEvent {
    data class OnAppBarMenuExpandedChange(val expanded: Boolean) : ClassworkUiEvent()

    data class OnDeleteCourseWork(val id: String) : ClassworkUiEvent()

    data class OnOpenDeleteCourseWorkDialogChange(val courseWork: CourseWork?) : ClassworkUiEvent()

    data class OnShowCreateCourseWorkBottomSheetChange(val showBottomSheet: Boolean) : ClassworkUiEvent()

    data object OnRefresh : ClassworkUiEvent()

    data object OnRetry : ClassworkUiEvent()

    data object UserMessageShown : ClassworkUiEvent()
}
