package app.edumate.data.mapper

import app.edumate.data.remote.dto.courseWorks.CourseWorkDto
import app.edumate.data.remote.dto.courseWorks.MultipleChoiceQuestionDto
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
