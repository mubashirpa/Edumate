package edumate.app.presentation.home

sealed class HomeUiEvent {
    data class OnOpenFabMenuChange(val open: Boolean) : HomeUiEvent()
}