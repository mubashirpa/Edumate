package edumate.app.presentation.createClass

import androidx.compose.ui.text.input.TextFieldValue
import edumate.app.core.UiText

data class CreateClassUiState(
    val createClassId: String? = null,
    val isLoading: Boolean = false,
    val name: TextFieldValue = TextFieldValue(""),
    val nameError: UiText? = null,
    val openProgressDialog: Boolean = false,
    val room: String = "",
    val section: String = "",
    val subject: String = "",
    val userMessage: UiText? = null,
)
