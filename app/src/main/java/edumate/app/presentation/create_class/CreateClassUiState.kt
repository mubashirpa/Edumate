package edumate.app.presentation.create_class

import com.google.firebase.auth.FirebaseUser
import edumate.app.core.UiText
import edumate.app.domain.model.courses.Course

data class CreateClassUiState(
    val course: Course = Course(
        name = "",
        section = "",
        room = "",
        subject = ""
    ),
    val currentUser: FirebaseUser? = null,
    val error: UiText? = null,
    val loading: Boolean = false,
    val nameError: UiText? = null,
    val openProgressDialog: Boolean = false,
    val progressDialogText: UiText = UiText.Empty,
    val userMessage: UiText? = null
)