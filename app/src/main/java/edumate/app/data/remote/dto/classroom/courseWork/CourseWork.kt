package edumate.app.data.remote.dto.classroom.courseWork

import edumate.app.data.remote.dto.classroom.AssigneeMode
import edumate.app.data.remote.dto.classroom.IndividualStudentsOptions
import edumate.app.data.remote.dto.classroom.Material
import kotlinx.serialization.Serializable

@Serializable
data class CourseWork(
    val alternateLink: String? = null,
    val assigneeMode: AssigneeMode? = null,
    val assignment: Assignment? = null,
    val associatedWithDeveloper: Boolean? = null,
    val courseId: String? = null,
    val creationTime: String? = null,
    val creatorUserId: String? = null,
    val description: String? = null,
    val dueDate: DueDate? = null,
    val dueTime: DueTime? = null,
    val gradeCategory: GradeCategory? = null,
    val id: String? = null,
    val individualStudentsOptions: IndividualStudentsOptions? = null,
    val materials: List<Material>? = null,
    val maxPoints: Int? = null,
    val multipleChoiceQuestion: MultipleChoiceQuestion? = null,
    val scheduledTime: String? = null,
    val state: CourseWorkState? = null,
    val submissionModificationMode: SubmissionModificationMode? = null,
    val title: String? = null,
    val topicId: String? = null,
    val updateTime: String? = null,
    val workType: CourseWorkType? = null,
)
