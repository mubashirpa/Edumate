package edumate.app.core

sealed class DataState {
    object UNKNOWN : DataState()
    object LOADING : DataState()
    data class ERROR(val message: UiText) : DataState()
    data class EMPTY(val message: UiText) : DataState()
    object SUCCESS : DataState()
}