package app.edumate.data.mapper

import app.edumate.data.remote.dto.courseWork.CourseWorkDto
import app.edumate.data.remote.dto.courseWork.CourseWorkTypeDto
import app.edumate.data.remote.dto.courseWork.MultipleChoiceQuestionDto
import app.edumate.data.remote.dto.courseWork.SubmissionModificationModeDto
import app.edumate.domain.model.courseWork.CourseWork
import app.edumate.domain.model.courseWork.CourseWorkType
import app.edumate.domain.model.courseWork.MultipleChoiceQuestion
import app.edumate.domain.model.courseWork.SubmissionModificationMode

fun CourseWorkDto.toCourseWorkDomainModel(): CourseWork =
    CourseWork(
        alternateLink = alternateLink,
        courseId = courseId,
        creationTime = creationTime,
        creatorUserId = creatorUserId,
        description = description,
        dueTime = dueTime,
        id = id,
        materials = materials?.map { it.toMaterialDomainModel() },
        maxPoints = maxPoints,
        multipleChoiceQuestion = multipleChoiceQuestion?.toMultipleChoiceQuestionDomainModel(),
        submissionModificationMode =
            submissionModificationMode?.let {
                enumValueOf<SubmissionModificationMode>(it.name)
            },
        title = title,
        updateTime = updateTime,
        workType = workType?.let { enumValueOf<CourseWorkType>(it.name) },
    )

fun MultipleChoiceQuestionDto.toMultipleChoiceQuestionDomainModel(): MultipleChoiceQuestion = MultipleChoiceQuestion(choices = choices)

fun CourseWork.toCourseWorkDto(): CourseWorkDto =
    CourseWorkDto(
        alternateLink = alternateLink,
        courseId = courseId,
        creationTime = creationTime,
        creatorUserId = creatorUserId,
        description = description,
        dueTime = dueTime,
        id = id,
        materials = materials?.map { it.toMaterialDto() },
        maxPoints = maxPoints,
        multipleChoiceQuestion = multipleChoiceQuestion?.toMultipleChoiceQuestionDto(),
        submissionModificationMode =
            submissionModificationMode?.let {
                enumValueOf<SubmissionModificationModeDto>(it.name)
            },
        title = title,
        updateTime = updateTime,
        workType = workType?.let { enumValueOf<CourseWorkTypeDto>(it.name) },
    )

fun MultipleChoiceQuestion.toMultipleChoiceQuestionDto(): MultipleChoiceQuestionDto = MultipleChoiceQuestionDto(choices = choices)
