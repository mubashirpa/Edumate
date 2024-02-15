package edumate.app.domain.model.classroom.courseWork

import edumate.app.domain.model.classroom.Material

data class CourseWork(
    val alternateLink: String? = null,
    val assignment: Assignment? = null,
    val creationTime: String? = null,
    val description: String? = null,
    val dueDate: DueDate? = null,
    val dueTime: DueTime? = null,
    val gradeCategory: GradeCategory? = null,
    val id: String? = null,
    val materials: List<Material>? = null,
    val maxPoints: Int? = null,
    val multipleChoiceQuestion: MultipleChoiceQuestion? = null,
    val scheduledTime: String? = null,
    val state: CourseWorkState? = null,
    val submissionModificationMode: SubmissionModificationMode? = null,
    val title: String? = null,
    val updateTime: String? = null,
    val workType: CourseWorkType? = null,
)
