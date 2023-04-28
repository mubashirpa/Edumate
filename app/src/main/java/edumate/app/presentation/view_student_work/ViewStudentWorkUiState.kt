package edumate.app.presentation.view_student_work

import edumate.app.core.DataState
import edumate.app.core.UiText
import edumate.app.domain.model.student_submission.StudentSubmission

data class ViewStudentWorkUiState(
    val appBarMenuExpanded: Boolean = false,
    val dataState: DataState = DataState.UNKNOWN,
    val grade: String = "",
    val openProgressDialog: Boolean = false,
    val openReturnDialog: Boolean = false,
    val refreshing: Boolean = false,
    val studentWork: StudentSubmission? = null,
    val userMessage: UiText? = null
)