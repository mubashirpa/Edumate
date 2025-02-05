package app.edumate.presentation.viewCourseWork

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import app.edumate.core.Result
import app.edumate.core.UiText
import app.edumate.domain.model.courseWork.CourseWork
import app.edumate.domain.model.material.Material
import app.edumate.domain.model.studentSubmission.StudentSubmission

data class ViewCourseWorkUiState(
    val courseWorkResult: Result<CourseWork> = Result.Empty(),
    val currentUserId: String? = null,
    val editShortAnswer: Boolean = false,
    val expandedAppBarDropdown: Boolean = false,
    val isRefreshing: Boolean = false,
    val multipleChoiceAnswer: String = "",
    val openProgressDialog: Boolean = false,
    val openTurnInDialog: Boolean = false,
    val openUnSubmitDialog: Boolean = false,
    val removeAttachmentIndex: Int? = null,
    val shortAnswer: TextFieldState = TextFieldState(),
    val showYourWorkBottomSheet: Boolean = false,
    val studentSubmissionAttachments: SnapshotStateList<Material> = mutableStateListOf(),
    val studentSubmissionResult: Result<StudentSubmission> = Result.Empty(),
    val userMessage: UiText? = null,
)
