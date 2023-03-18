package edumate.app.data.remote.mapper

import edumate.app.data.remote.dto.CoursesDto
import edumate.app.domain.model.courses.Course

fun CoursesDto.toCourse(): Course {
    return Course(
        alternateLink,
        courseState,
        creationTime,
        description,
        descriptionHeading,
        id,
        name,
        ownerId,
        room,
        section,
        students,
        subject,
        teachers,
        updateTime
    )
}

fun Course.toCoursesDto(): CoursesDto {
    return CoursesDto(
        alternateLink,
        courseState,
        creationTime,
        description,
        descriptionHeading,
        id,
        name,
        ownerId,
        room,
        section,
        students,
        subject,
        teachers,
        updateTime
    )
}