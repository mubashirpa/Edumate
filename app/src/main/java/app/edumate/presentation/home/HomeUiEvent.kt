package app.edumate.presentation.home

sealed class HomeUiEvent {
    data class JoinCourse(
        val courseId: String,
    ) : HomeUiEvent()

    data class OnAppBarDropdownExpandedChange(
        val expanded: Boolean,
    ) : HomeUiEvent()

    data class OnShowAddCourseBottomSheetChange(
        val show: Boolean,
    ) : HomeUiEvent()

    data class OnShowJoinCourseBottomSheetChange(
        val show: Boolean,
    ) : HomeUiEvent()

    data object OnRefresh : HomeUiEvent()

    data object OnRetry : HomeUiEvent()

    data object UserMessageShown : HomeUiEvent()
}
