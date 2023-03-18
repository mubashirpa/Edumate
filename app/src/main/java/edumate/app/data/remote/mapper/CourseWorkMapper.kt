package edumate.app.data.remote.mapper

import edumate.app.data.remote.dto.CourseWorkDto
import edumate.app.domain.model.course_work.CourseWork

fun CourseWorkDto.toCourseWork(): CourseWork {
    return CourseWork(
        courseId,
        creationTime,
        creatorUserId,
        description,
        dueTime,
        id,
        materials,
        maxPoints,
        scheduledTime,
        title,
        updateTime,
        workType
    )
}

fun CourseWork.toCourseWorkDto(): CourseWorkDto {
    return CourseWorkDto(
        courseId,
        creationTime,
        creatorUserId,
        description,
        dueTime,
        id,
        materials,
        maxPoints,
        scheduledTime,
        title,
        updateTime,
        workType
    )
}