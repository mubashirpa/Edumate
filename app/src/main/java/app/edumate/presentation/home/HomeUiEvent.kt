package app.edumate.presentation.home

sealed class HomeUiEvent {
    data class OnAppBarDropdownExpandedChange(
        val expanded: Boolean,
    ) : HomeUiEvent()

    data class OnShowAddCourseBottomSheetChange(
        val show: Boolean,
    ) : HomeUiEvent()

    data class OnShowCreateCourseBottomSheetChange(
        val show: Boolean,
    ) : HomeUiEvent()

    data class OnShowJoinCourseBottomSheetChange(
        val show: Boolean,
    ) : HomeUiEvent()

    object OnRefresh : HomeUiEvent()
}
