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
        id = id,
        courseId = courseId,
        creatorUserId = creatorUserId,
        alternateLink = alternateLink,
        creationTime = creationTime,
        description = description,
        dueTime = dueTime,
        materials = materials?.map { it.toMaterialDomainModel() },
        maxPoints = maxPoints,
        multipleChoiceQuestion = multipleChoiceQuestion?.toMultipleChoiceQuestionDomainModel(),
        title = title,
        updateTime = updateTime,
        submissionModificationMode =
            submissionModificationMode?.let {
                enumValueOf<SubmissionModificationMode>(it.name)
            },
        workType = workType?.let { enumValueOf<CourseWorkType>(it.name) },
    )

fun MultipleChoiceQuestionDto.toMultipleChoiceQuestionDomainModel(): MultipleChoiceQuestion = MultipleChoiceQuestion(choices = choices)

fun CourseWork.toCourseWorkDto(): CourseWorkDto =
    CourseWorkDto(
        id = id,
        courseId = courseId,
        creatorUserId = creatorUserId,
        alternateLink = alternateLink,
        creationTime = creationTime,
        description = description,
        dueTime = dueTime,
        materials = materials?.map { it.toMaterialDto() },
        maxPoints = maxPoints,
        multipleChoiceQuestion = multipleChoiceQuestion?.toMultipleChoiceQuestionDto(),
        title = title,
        updateTime = updateTime,
        submissionModificationMode =
            submissionModificationMode?.let {
                enumValueOf<SubmissionModificationModeDto>(it.name)
            },
        workType = workType?.let { enumValueOf<CourseWorkTypeDto>(it.name) },
    )

fun MultipleChoiceQuestion.toMultipleChoiceQuestionDto(): MultipleChoiceQuestionDto = MultipleChoiceQuestionDto(choices = choices)
