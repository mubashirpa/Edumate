package app.edumate.data.mapper

import app.edumate.data.remote.dto.courseWork.CourseWorkTypeDto
import app.edumate.data.remote.dto.studentSubmission.AssignmentSubmissionDto
import app.edumate.data.remote.dto.studentSubmission.QuestionSubmissionDto
import app.edumate.data.remote.dto.studentSubmission.StudentSubmissionDto
import app.edumate.data.remote.dto.studentSubmission.StudentSubmissionListDto
import app.edumate.data.remote.dto.studentSubmission.SubmissionStateDto
import app.edumate.domain.model.courseWork.CourseWorkType
import app.edumate.domain.model.studentSubmission.AssignmentSubmission
import app.edumate.domain.model.studentSubmission.QuestionSubmission
import app.edumate.domain.model.studentSubmission.StudentSubmission
import app.edumate.domain.model.studentSubmission.StudentSubmissionList
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

fun StudentSubmission.toStudentSubmissionDto(): StudentSubmissionDto =
    StudentSubmissionDto(
        alternateLink = alternateLink,
        assignedGrade = assignedGrade,
        assignmentSubmission = assignmentSubmission?.toAssignmentSubmissionDto(),
        courseId = courseId,
        courseWorkId = courseWorkId,
        courseWorkType = courseWorkType?.let { enumValueOf<CourseWorkTypeDto>(it.name) },
        creationTime = creationTime,
        id = id,
        late = late,
        multipleChoiceSubmission = multipleChoiceSubmission?.toQuestionSubmissionDto(),
        shortAnswerSubmission = shortAnswerSubmission?.toQuestionSubmissionDto(),
        state = state?.let { enumValueOf<SubmissionStateDto>(it.name) },
        updateTime = updateTime,
        userId = userId,
    )

fun StudentSubmissionListDto.toStudentSubmissionListDomainModel(): StudentSubmissionList =
    StudentSubmissionList(
        alternateLink = alternateLink,
        assignedGrade = assignedGrade,
        assignmentSubmission = assignmentSubmission?.toAssignmentSubmissionDomainModel(),
        courseId = courseId,
        courseWork = courseWork?.toCourseWorkDomainModel(),
        courseWorkId = courseWorkId,
        courseWorkType = courseWorkType?.let { enumValueOf<CourseWorkType>(it.name) },
        creationTime = creationTime,
        id = id,
        late = late,
        multipleChoiceSubmission = multipleChoiceSubmission?.toQuestionSubmissionDomainModel(),
        shortAnswerSubmission = shortAnswerSubmission?.toQuestionSubmissionDomainModel(),
        state = state?.let { enumValueOf<SubmissionState>(it.name) },
        updateTime = updateTime,
        user = user?.toUserDomainModel(),
        userId = userId,
    )

fun AssignmentSubmissionDto.toAssignmentSubmissionDomainModel(): AssignmentSubmission =
    AssignmentSubmission(attachments = attachments?.map { it.toMaterialDomainModel() })

fun AssignmentSubmission.toAssignmentSubmissionDto(): AssignmentSubmissionDto =
    AssignmentSubmissionDto(attachments = attachments?.map { it.toMaterialDto() })

fun QuestionSubmissionDto.toQuestionSubmissionDomainModel(): QuestionSubmission = QuestionSubmission(answer = answer)

fun QuestionSubmission.toQuestionSubmissionDto(): QuestionSubmissionDto = QuestionSubmissionDto(answer = answer)
