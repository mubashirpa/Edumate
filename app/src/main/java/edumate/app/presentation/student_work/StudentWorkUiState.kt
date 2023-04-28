package edumate.app.presentation.student_work

import edumate.app.core.DataState
import edumate.app.domain.model.User
import edumate.app.domain.model.student_submission.StudentSubmission

data class StudentWorkUiState(
    val assignedStudents: List<User> = emptyList(),
    val dataState: DataState = DataState.UNKNOWN,
    val studentSubmissions: List<StudentSubmission> = emptyList()
)