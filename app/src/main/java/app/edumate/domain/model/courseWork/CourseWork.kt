package app.edumate.domain.model.courseWork

import app.edumate.domain.model.material.Material

data class CourseWork(
    val id: String? = null,
    val courseId: String? = null,
    val creatorUserId: String? = null,
    val alternateLink: String? = null,
    val creationTime: String? = null,
    val description: String? = null,
    val dueTime: String? = null,
    val materials: List<Material>? = null,
    val maxPoints: Int? = null,
    val multipleChoiceQuestion: MultipleChoiceQuestion? = null,
    val title: String? = null,
    val updateTime: String? = null,
    val submissionModificationMode: SubmissionModificationMode? = null,
    val workType: CourseWorkType? = null,
)
