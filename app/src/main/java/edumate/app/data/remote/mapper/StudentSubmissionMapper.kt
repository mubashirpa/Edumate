package edumate.app.data.remote.mapper

import edumate.app.data.remote.dto.StudentSubmissionDto
import edumate.app.domain.model.student_submission.StudentSubmission

fun StudentSubmissionDto.toStudentSubmission(): StudentSubmission {
    return StudentSubmission(
        courseId,
        courseWorkId,
        id,
        userId,
        creationTime,
        updateTime,
        late,
        assignedGrade,
        alternateLink,
        courseWorkType,
        assignmentSubmission,
        shortAnswerSubmission,
        multipleChoiceSubmission
    )
}

fun StudentSubmission.toStudentSubmissionDto(): StudentSubmissionDto {
    return StudentSubmissionDto(
        courseId,
        courseWorkId,
        id,
        userId,
        creationTime,
        updateTime,
        late,
        assignedGrade,
        alternateLink,
        courseWorkType,
        assignmentSubmission,
        shortAnswerSubmission,
        multipleChoiceSubmission
    )
}