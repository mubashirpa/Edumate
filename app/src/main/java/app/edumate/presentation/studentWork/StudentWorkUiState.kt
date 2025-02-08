package app.edumate.presentation.studentWork

import app.edumate.core.Result
import app.edumate.core.UiText
import app.edumate.domain.model.studentSubmission.StudentSubmissionList

data class StudentWorkUiState(
    val isRefreshing: Boolean = false,
    val studentSubmissionsResult: Result<List<StudentSubmissionList>> = Result.Empty(),
    val userMessage: UiText? = null,
)
