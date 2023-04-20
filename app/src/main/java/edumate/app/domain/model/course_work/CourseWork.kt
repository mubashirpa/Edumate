package edumate.app.domain.model.course_work

import java.util.*

data class CourseWork(
    val courseId: String = "",
    val id: String = "",
    val title: String = "",
    val description: String? = null,
    val materials: List<Material> = listOf(),
    val state: CourseWorkState = CourseWorkState.COURSE_WORK_STATE_UNSPECIFIED,
    val alternateLink: String = "",
    val creationTime: Date? = null,
    val updateTime: Date? = null,
    val dueTime: Date? = null,
    val scheduledTime: Date? = null,
    val maxPoints: Int? = null,
    val workType: CourseWorkType = CourseWorkType.COURSE_WORK_TYPE_UNSPECIFIED,
    val assigneeMode: AssigneeMode = AssigneeMode.ASSIGNEE_MODE_UNSPECIFIED,
    val individualStudentsOptions: IndividualStudentsOptions? = null,
    val submissionModificationMode: SubmissionModificationMode = SubmissionModificationMode.SUBMISSION_MODIFICATION_MODE_UNSPECIFIED,
    val creatorUserId: String = "",
    val assignment: Assignment? = null,
    val multipleChoiceQuestion: MultipleChoiceQuestion? = null
)