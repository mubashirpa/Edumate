package app.edumate.presentation.viewStudentSubmission

import androidx.compose.foundation.text.input.TextFieldState
import app.edumate.core.Result
import app.edumate.core.UiText
import app.edumate.domain.model.studentSubmission.StudentSubmission

data class ViewStudentSubmissionUiState(
    val expandedAppBarDropdown: Boolean = false,
    val grade: TextFieldState = TextFieldState(),
    val isRefreshing: Boolean = false,
    val openProgressDialog: Boolean = false,
    val openReturnDialog: Boolean = false,
    val studentSubmissionResult: Result<StudentSubmission> = Result.Empty(),
    val title: String = "",
    val userMessage: UiText? = null,
)
