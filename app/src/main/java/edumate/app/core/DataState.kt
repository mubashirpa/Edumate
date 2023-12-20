package edumate.app.core

sealed class DataState {
    data object UNKNOWN : DataState()
    data object LOADING : DataState()
    data class ERROR(val message: UiText) : DataState()
    data class EMPTY(val message: UiText) : DataState()
    data object SUCCESS : DataState()
}