package edumate.app.presentation.classwork

import com.google.firebase.auth.FirebaseUser
import edumate.app.core.UiText
import edumate.app.domain.model.course_work.CourseWork

data class ClassworkUiState(
    val classWorks: List<CourseWork> = listOf(),
    val currentUser: FirebaseUser? = null,
    val dataState: DataState = DataState.UNKNOWN,
    val openFabMenu: Boolean = false,
    val refreshing: Boolean = false,
    val userMessage: UiText? = null
)