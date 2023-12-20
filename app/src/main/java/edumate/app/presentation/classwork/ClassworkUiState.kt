package edumate.app.presentation.classwork

import com.google.firebase.auth.FirebaseUser
import edumate.app.core.DataState
import edumate.app.core.UiText
import edumate.app.domain.model.course_work.CourseWork

data class ClassworkUiState(
    val appBarMenuExpanded: Boolean = false,
    val classwork: List<CourseWork> = listOf(),
    val currentUser: FirebaseUser? = null,
    val dataState: DataState = DataState.UNKNOWN,
    val deleteClasswork: CourseWork? = null,
    val openFabMenu: Boolean = false,
    val openProgressDialog: Boolean = false,
    val refreshing: Boolean = false,
    val userMessage: UiText? = null,
)
