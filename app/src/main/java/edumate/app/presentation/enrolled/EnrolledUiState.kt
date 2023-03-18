package edumate.app.presentation.enrolled

import com.google.firebase.auth.FirebaseUser
import edumate.app.core.UiText
import edumate.app.domain.model.courses.Course

data class EnrolledUiState(
    val classes: List<Course> = emptyList(),
    val currentUser: FirebaseUser? = null,
    val error: UiText? = null,
    val loading: Boolean = false,
    val openProgressDialog: Boolean = false,
    val success: Boolean = false,
    val userMessage: UiText? = null
)