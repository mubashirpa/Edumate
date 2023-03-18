package edumate.app.domain.model.course_work

import java.util.*

data class CourseWork(
    val courseId: String = "",
    val creationTime: Date? = null,
    val creatorUserId: String = "",
    var description: String? = "",
    var dueTime: Date? = null,
    val id: String = "",
    var materials: List<Material> = listOf(),
    var maxPoints: Int = 0,
    var scheduledTime: Date? = null,
    var title: String = "",
    val updateTime: Date? = null,
    val workType: CourseWorkType = CourseWorkType.COURSE_WORK_TYPE_UNSPECIFIED
)