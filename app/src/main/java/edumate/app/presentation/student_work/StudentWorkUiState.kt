package edumate.app.presentation.student_work

import edumate.app.core.DataState
import edumate.app.core.UiText
import edumate.app.domain.model.student_submissions.StudentSubmission
import edumate.app.domain.model.user_profiles.UserProfile

data class StudentWorkUiState(
    val assignedStudents: List<UserProfile> = emptyList(),
    val dataState: DataState = DataState.UNKNOWN,
    val refreshing: Boolean = false,
    val studentSubmissions: List<StudentSubmission> = emptyList(),
    val userMessage: UiText? = null
)