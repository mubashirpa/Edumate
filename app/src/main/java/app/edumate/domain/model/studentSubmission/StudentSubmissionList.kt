package app.edumate.domain.model.studentSubmission

import app.edumate.domain.model.courseWork.CourseWork
import app.edumate.domain.model.courseWork.CourseWorkType
import app.edumate.domain.model.user.User

data class StudentSubmissionList(
    val alternateLink: String? = null,
    val assignedGrade: Int? = null,
    val assignmentSubmission: AssignmentSubmission? = null,
    val courseId: String? = null,
    val courseWork: CourseWork? = null,
    val courseWorkId: String? = null,
    val courseWorkType: CourseWorkType? = null,
    val creationTime: String? = null,
    val id: String? = null,
    val late: Boolean? = null,
    val multipleChoiceSubmission: QuestionSubmission? = null,
    val shortAnswerSubmission: QuestionSubmission? = null,
    val state: SubmissionState? = null,
    val updateTime: String? = null,
    val user: User? = null,
    val userId: String? = null,
)
