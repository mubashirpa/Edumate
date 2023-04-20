package edumate.app.presentation.student_work

import edumate.app.domain.model.student_submission.StudentSubmission

data class StudentWorkUiState(
    val studentSubmissions: List<StudentSubmission> = listOf()
)