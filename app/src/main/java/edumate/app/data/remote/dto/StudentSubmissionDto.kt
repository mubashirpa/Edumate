package edumate.app.data.remote.dto

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ServerTimestamp
import edumate.app.core.FirebaseConstants
import edumate.app.domain.model.course_work.CourseWorkType
import edumate.app.domain.model.student_submission.AssignmentSubmission
import edumate.app.domain.model.student_submission.MultipleChoiceSubmission
import edumate.app.domain.model.student_submission.ShortAnswerSubmission
import edumate.app.domain.model.student_submission.SubmissionState
import java.util.*
import kotlin.collections.HashMap

data class StudentSubmissionDto(
    val courseId: String = "",
    val courseWorkId: String = "",
    val id: String = "",
    val userId: String = "",
    @ServerTimestamp
    val creationTime: Date? = null,
    @ServerTimestamp
    val updateTime: Date? = null,
    val state: SubmissionState = SubmissionState.SUBMISSION_STATE_UNSPECIFIED,
    val late: Boolean = false,
    val assignedGrade: Int? = null,
    val alternateLink: String = "",
    val courseWorkType: CourseWorkType = CourseWorkType.COURSE_WORK_TYPE_UNSPECIFIED,
    val assignmentSubmission: AssignmentSubmission? = null,
    val shortAnswerSubmission: ShortAnswerSubmission? = null,
    val multipleChoiceSubmission: MultipleChoiceSubmission? = null
) {
    @Exclude
    fun toMap(): HashMap<String, Any?> {
        val hashMap: HashMap<String, Any?> = hashMapOf(
            FirebaseConstants.Firestore.COURSE_ID to courseId,
            FirebaseConstants.Firestore.COURSE_WORK_ID to courseWorkId,
            FirebaseConstants.Firestore.ID to id,
            FirebaseConstants.Firestore.USER_ID to userId,
            FirebaseConstants.Firestore.CREATION_TIME to (
                creationTime
                    ?: FieldValue.serverTimestamp()
                ),
            FirebaseConstants.Firestore.UPDATE_TIME to FieldValue.serverTimestamp(),
            FirebaseConstants.Firestore.STATE to state,
            FirebaseConstants.Firestore.LATE to late,
            FirebaseConstants.Firestore.ASSIGNED_GRADE to assignedGrade,
            FirebaseConstants.Firestore.ALTERNATE_LINK to alternateLink,
            FirebaseConstants.Firestore.COURSE_WORK_TYPE to courseWorkType
        )
        if (assignmentSubmission != null) {
            hashMap[FirebaseConstants.Firestore.ASSIGNMENT_SUBMISSION] = assignmentSubmission
        }
        if (shortAnswerSubmission != null) {
            hashMap[FirebaseConstants.Firestore.SHORT_ANSWER_SUBMISSION] = shortAnswerSubmission
        }
        if (multipleChoiceSubmission != null) {
            hashMap[FirebaseConstants.Firestore.MULTIPLE_CHOICE_SUBMISSION] =
                multipleChoiceSubmission
        }
        return hashMap
    }
}