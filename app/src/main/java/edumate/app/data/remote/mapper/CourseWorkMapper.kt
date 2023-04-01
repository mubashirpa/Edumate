package edumate.app.data.remote.mapper

import edumate.app.data.remote.dto.CourseWorkDto
import edumate.app.domain.model.course_work.CourseWork

fun CourseWorkDto.toCourseWork(): CourseWork {
    return CourseWork(
        courseId,
        id,
        title,
        description,
        materials,
        state,
        creationTime,
        updateTime,
        dueTime,
        scheduledTime,
        maxPoints,
        workType,
        creatorUserId,
        multipleChoiceQuestion
    )
}

fun CourseWork.toCourseWorkDto(): CourseWorkDto {
    return CourseWorkDto(
        courseId,
        id,
        title,
        description,
        materials,
        state,
        creationTime,
        updateTime,
        dueTime,
        scheduledTime,
        maxPoints,
        workType,
        creatorUserId,
        multipleChoiceQuestion
    )
}