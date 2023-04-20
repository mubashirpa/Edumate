package edumate.app.data.remote.dto

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ServerTimestamp
import edumate.app.core.FirebaseConstants
import edumate.app.domain.model.course_work.AssigneeMode
import edumate.app.domain.model.course_work.Assignment
import edumate.app.domain.model.course_work.CourseWorkState
import edumate.app.domain.model.course_work.CourseWorkType
import edumate.app.domain.model.course_work.IndividualStudentsOptions
import edumate.app.domain.model.course_work.Material
import edumate.app.domain.model.course_work.MultipleChoiceQuestion
import edumate.app.domain.model.course_work.SubmissionModificationMode
import java.util.Date

data class CourseWorkDto(
    val courseId: String = "",
    val id: String = "",
    val title: String = "",
    val description: String? = null,
    val materials: List<Material> = listOf(),
    val state: CourseWorkState = CourseWorkState.COURSE_WORK_STATE_UNSPECIFIED,
    val alternateLink: String = "",
    @ServerTimestamp
    val creationTime: Date? = null,
    @ServerTimestamp
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
) {
    @Exclude
    fun toMap(): HashMap<String, Any?> {
        val hashMap: HashMap<String, Any?> = hashMapOf(
            FirebaseConstants.Firestore.COURSE_ID to courseId,
            FirebaseConstants.Firestore.ID to id,
            FirebaseConstants.Firestore.TITLE to title,
            FirebaseConstants.Firestore.DESCRIPTION to description,
            FirebaseConstants.Firestore.MATERIALS to materials,
            FirebaseConstants.Firestore.STATE to state,
            FirebaseConstants.Firestore.ALTERNATE_LINK to alternateLink,
            FirebaseConstants.Firestore.CREATION_TIME to (
                creationTime
                    ?: FieldValue.serverTimestamp()
                ),
            FirebaseConstants.Firestore.UPDATE_TIME to FieldValue.serverTimestamp(),
            FirebaseConstants.Firestore.DUE_TIME to dueTime,
            FirebaseConstants.Firestore.SCHEDULED_TIME to (
                scheduledTime
                    ?: FieldValue.serverTimestamp()
                ),
            FirebaseConstants.Firestore.MAX_POINTS to maxPoints,
            FirebaseConstants.Firestore.WORK_TYPE to workType,
            FirebaseConstants.Firestore.ASSIGNEE_MODE to assigneeMode,
            FirebaseConstants.Firestore.SUBMISSION_MODIFICATION_MODE to submissionModificationMode,
            FirebaseConstants.Firestore.CREATOR_USER_ID to creatorUserId
        )
        if (assigneeMode == AssigneeMode.INDIVIDUAL_STUDENTS) {
            hashMap[FirebaseConstants.Firestore.INDIVIDUAL_STUDENTS_OPTIONS] =
                individualStudentsOptions
        }
        if (assignment != null) {
            hashMap[FirebaseConstants.Firestore.ASSIGNMENT] = assignment
        }
        if (multipleChoiceQuestion != null) {
            hashMap[FirebaseConstants.Firestore.MULTIPLE_CHOICE_QUESTION] = multipleChoiceQuestion
        }
        return hashMap
    }
}