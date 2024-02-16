package edumate.app.presentation.classwork

import com.google.firebase.auth.FirebaseUser
import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.domain.model.classroom.courseWork.CourseWork

data class ClassworkUiState(
    val appBarMenuExpanded: Boolean = false,
    val courseWorkResult: Result<List<CourseWork>> = Result.Empty(),
    val currentUser: FirebaseUser? = null,
    val deleteCourseWork: CourseWork? = null,
    val openProgressDialog: Boolean = false,
    val refreshing: Boolean = false,
    val showCreateCourseWorkBottomSheet: Boolean = false,
    val userMessage: UiText? = null,
)
