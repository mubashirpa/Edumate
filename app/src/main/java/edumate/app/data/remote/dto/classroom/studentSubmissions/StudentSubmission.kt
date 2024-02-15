package edumate.app.data.remote.dto.classroom.studentSubmissions

import edumate.app.data.remote.dto.classroom.courseWork.CourseWorkType
import kotlinx.serialization.Serializable

@Serializable
data class StudentSubmission(
    val alternateLink: String? = null,
    val assignedGrade: Int? = null,
    val assignmentSubmission: AssignmentSubmission? = null,
    val associatedWithDeveloper: Boolean? = null,
    val courseId: String? = null,
    val courseWorkId: String? = null,
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
