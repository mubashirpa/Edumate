package edumate.app.data.remote.mapper

import edumate.app.data.remote.dto.CourseDto
import edumate.app.domain.model.courses.Course

fun CourseDto.toCourse(): Course {
    return Course(
        id,
        name,
        section,
        descriptionHeading,
        description,
        room,
        ownerId,
        creationTime,
        updateTime,
        enrollmentCode,
        courseState,
        alternateLink,
        courseGroupId,
        teacherGroupId,
        guardiansEnabled,
        calendarId,
        gradeBookSettings,
        creatorProfile
    )
}

fun Course.toCoursesDto(): CourseDto {
    return CourseDto(
        id,
        name,
        section,
        descriptionHeading,
        description,
        room,
        ownerId,
        creationTime,
        updateTime,
        enrollmentCode,
        courseState,
        alternateLink,
        courseGroupId,
        teacherGroupId,
        guardiansEnabled,
        calendarId,
        gradeBookSettings,
        creatorProfile
    )
}