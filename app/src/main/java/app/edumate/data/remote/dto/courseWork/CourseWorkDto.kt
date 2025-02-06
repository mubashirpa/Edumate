package app.edumate.data.remote.dto.courseWork

import app.edumate.data.remote.dto.material.MaterialDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CourseWorkDto(
    @SerialName("alternate_link")
    val alternateLink: String? = null,
    @SerialName("course_id")
    val courseId: String? = null,
    @SerialName("creation_time")
    val creationTime: String? = null,
    @SerialName("creator_user_id")
    val creatorUserId: String? = null,
    val description: String? = null,
    @SerialName("due_time")
    val dueTime: String? = null,
    val id: String? = null,
    val materials: List<MaterialDto>? = null,
    @SerialName("max_points")
    val maxPoints: Int? = null,
    @SerialName("multiple_choice_question")
    val multipleChoiceQuestion: MultipleChoiceQuestionDto? = null,
    @SerialName("submission_modification_mode")
    val submissionModificationMode: SubmissionModificationModeDto? = null,
    val title: String? = null,
    @SerialName("update_time")
    val updateTime: String? = null,
    @SerialName("work_type")
    val workType: CourseWorkTypeDto? = null,
)
