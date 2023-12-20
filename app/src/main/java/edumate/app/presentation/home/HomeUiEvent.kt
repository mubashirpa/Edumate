package edumate.app.presentation.home

sealed class HomeUiEvent {
    data class OnAppBarMenuExpandedChange(val expanded: Boolean) : HomeUiEvent()

    data class OnOpenFabMenuChange(val open: Boolean) : HomeUiEvent()

    data class OnRefreshChange(val refreshing: Boolean) : HomeUiEvent()
}
