package edumate.app.domain.model.course_work

import java.util.*

data class CourseWork(
    val courseId: String = "",
    val id: String = "",
    var title: String = "",
    var description: String? = null,
    var materials: List<Material> = listOf(),
    var state: CourseWorkState = CourseWorkState.COURSE_WORK_STATE_UNSPECIFIED,
    val creationTime: Date? = null,
    val updateTime: Date? = null,
    var dueTime: Date? = null,
    var scheduledTime: Date? = null,
    var maxPoints: Int? = null,
    val workType: CourseWorkType = CourseWorkType.COURSE_WORK_TYPE_UNSPECIFIED,
    val creatorUserId: String = "",
    val multipleChoiceQuestion: MultipleChoiceQuestion? = null
)