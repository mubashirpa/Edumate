package edumate.app.presentation.create_class

import edumate.app.core.UiText

data class CreateClassUiState(
    val loading: Boolean = false,
    val name: String = "",
    val nameError: UiText? = null,
    val openProgressDialog: Boolean = false,
    val room: String = "",
    val section: String = "",
    val subject: String = "",
    val userMessage: UiText? = null
)