package app.edumate.data.mapper

import app.edumate.data.remote.dto.studentSubmission.AssignmentSubmissionDto
import app.edumate.data.remote.dto.studentSubmission.QuestionSubmissionDto
import app.edumate.data.remote.dto.studentSubmission.StudentSubmissionDto
import app.edumate.domain.model.courseWork.CourseWorkType
import app.edumate.domain.model.studentSubmission.AssignmentSubmission
import app.edumate.domain.model.studentSubmission.QuestionSubmission
import app.edumate.domain.model.studentSubmission.StudentSubmission
import app.edumate.domain.model.studentSubmission.SubmissionState

fun StudentSubmissionDto.toStudentSubmissionDomainModel(): StudentSubmission =
    StudentSubmission(
        alternateLink = alternateLink,
        assignedGrade = assignedGrade,
        assignmentSubmission = assignmentSubmission?.toAssignmentSubmissionDomainModel(),
        courseId = courseId,
        courseWorkId = courseWorkId,
        courseWorkType = courseWorkType?.let { enumValueOf<CourseWorkType>(it.name) },
        creationTime = creationTime,
        id = id,
        late = late,
        multipleChoiceSubmission = multipleChoiceSubmission?.toQuestionSubmissionDomainModel(),
        shortAnswerSubmission = shortAnswerSubmission?.toQuestionSubmissionDomainModel(),
        state = state?.let { enumValueOf<SubmissionState>(it.name) },
        updateTime = updateTime,
        userId = userId,
    )

fun AssignmentSubmissionDto.toAssignmentSubmissionDomainModel(): AssignmentSubmission =
    AssignmentSubmission(attachments = attachments?.map { it.toMaterialDomainModel() })

fun AssignmentSubmission.toAssignmentSubmissionDomainModel(): AssignmentSubmissionDto =
    AssignmentSubmissionDto(attachments = attachments?.map { it.toMaterialDto() })

fun QuestionSubmissionDto.toQuestionSubmissionDomainModel(): QuestionSubmission = QuestionSubmission(answer = answer)
