package edumate.app.domain.model.student_submission

import edumate.app.domain.model.course_work.CourseWorkType
import java.util.*

data class StudentSubmission(
    val courseId: String = "",
    val courseWorkId: String = "",
    val id: String = "",
    val userId: String = "",
    val creationTime: Date? = null,
    val updateTime: Date? = null,
    val late: Boolean = false,
    val assignedGrade: Int? = null,
    val alternateLink: String = "",
    val courseWorkType: CourseWorkType = CourseWorkType.COURSE_WORK_TYPE_UNSPECIFIED,
    val assignmentSubmission: AssignmentSubmission? = null,
    val shortAnswerSubmission: ShortAnswerSubmission? = null,
    val multipleChoiceSubmission: MultipleChoiceSubmission? = null
)