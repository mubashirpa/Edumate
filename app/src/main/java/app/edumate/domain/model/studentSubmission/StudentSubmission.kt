package app.edumate.domain.model.studentSubmission

import app.edumate.domain.model.courseWork.CourseWorkType

data class StudentSubmission(
    val alternateLink: String? = null,
    val assignedGrade: Int? = null,
    val assignmentSubmission: AssignmentSubmission? = null,
    val courseId: String? = null,
    val courseWorkId: String? = null,
    val courseWorkType: CourseWorkType? = null,
    val creationTime: String? = null,
    val id: String? = null,
    val late: Boolean? = null,
    val multipleChoiceSubmission: QuestionSubmission? = null,
    val shortAnswerSubmission: QuestionSubmission? = null,
    val state: SubmissionState? = null,
    val updateTime: String? = null,
    val userId: String? = null,
)
