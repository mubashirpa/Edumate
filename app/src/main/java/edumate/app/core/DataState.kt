package edumate.app.core

@Deprecated("Deprecated class and will be removed later")
sealed interface DataState {
    /**
     * Empty state when the screen is first shown
     */
    data object UNKNOWN : DataState

    /**
     * Still loading
     */
    data object LOADING : DataState

    /**
     * There was an error fetching data
     */
    data class ERROR(val message: UiText) : DataState

    /**
     * Data has been fetched but empty
     */
    data class EMPTY(val message: UiText) : DataState

    /**
     * Data has been fetched
     */
    data object SUCCESS : DataState
}
