package edumate.app.presentation.home

sealed class HomeUiEvent {
    data class OnAppBarDropdownExpandedChange(val expanded: Boolean) : HomeUiEvent()

    data class OnRefreshChange(val refreshing: Boolean) : HomeUiEvent()

    data class OnShowAddCourseBottomSheetChange(val showBottomSheet: Boolean) : HomeUiEvent()
}
