package app.edumate.data.remote.dto.studentSubmission

import app.edumate.data.remote.dto.courseWork.CourseWorkDto
import app.edumate.data.remote.dto.courseWork.CourseWorkTypeDto
import app.edumate.data.remote.dto.user.UserDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudentSubmissionDto(
    @SerialName("alternate_link")
    val alternateLink: String? = null,
    @SerialName("assigned_grade")
    val assignedGrade: Int? = null,
    @SerialName("assignment_submission")
    val assignmentSubmission: AssignmentSubmissionDto? = null,
    @SerialName("course_id")
    val courseId: String? = null,
    @SerialName("course_work")
    val courseWorkDto: CourseWorkDto? = null,
    @SerialName("course_work_id")
    val courseWorkId: String? = null,
    @SerialName("course_work_type")
    val courseWorkType: CourseWorkTypeDto? = null,
    @SerialName("creation_time")
    val creationTime: String? = null,
    @SerialName("draft_grade")
    val draftGrade: Int? = null,
    val id: String? = null,
    val late: Boolean? = null,
    @SerialName("multiple_choice_submission")
    val multipleChoiceSubmission: QuestionSubmissionDto? = null,
    @SerialName("short_answer_submission")
    val shortAnswerSubmission: QuestionSubmissionDto? = null,
    val state: SubmissionStateDto? = null,
    @SerialName("update_time")
    val updateTime: String? = null,
    @SerialName("user")
    val user: UserDto? = null,
    @SerialName("user_id")
    val userId: String? = null,
)
