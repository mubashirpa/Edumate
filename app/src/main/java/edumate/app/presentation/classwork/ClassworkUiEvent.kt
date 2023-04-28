package edumate.app.presentation.classwork

import edumate.app.domain.model.course_work.CourseWork

sealed class ClassworkUiEvent {
    data class OnAppBarMenuExpandedChange(val expanded: Boolean) : ClassworkUiEvent()
    data class OnDeleteClasswork(val classworkId: String) : ClassworkUiEvent()
    data class OnOpenDeleteClassworkDialogChange(val classwork: CourseWork?) : ClassworkUiEvent()
    data class OnOpenFabMenuChange(val open: Boolean) : ClassworkUiEvent()
    object OnRefresh : ClassworkUiEvent()
    object OnRetry : ClassworkUiEvent()
    object UserMessageShown : ClassworkUiEvent()
}