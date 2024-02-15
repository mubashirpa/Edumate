package edumate.app.domain.model.classroom.studentSubmissions

import edumate.app.domain.model.classroom.courseWork.CourseWorkType

data class StudentSubmission(
    val alternateLink: String? = null,
    val assignedGrade: Int? = null,
    val assignmentSubmission: AssignmentSubmission? = null,
    val courseWorkType: CourseWorkType? = null,
    val creationTime: String? = null,
    val draftGrade: Int? = null,
    val id: String? = null,
    val late: Boolean? = null,
    val multipleChoiceSubmission: MultipleChoiceSubmission? = null,
    val shortAnswerSubmission: ShortAnswerSubmission? = null,
    val state: SubmissionState? = null,
    val updateTime: String? = null,
    val userId: String? = null,
)
