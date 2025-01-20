package app.edumate.data.mapper

import app.edumate.data.remote.dto.courses.CourseDto
import app.edumate.domain.model.Course

fun CourseDto.toCourseDomainModel(): Course =
    Course(
        alternateLink = alternateLink,
        creationTime = creationTime,
        description = description,
        enrollmentCode = enrollmentCode,
        id = id,
        name = name,
        owner = owner?.toUserDomainModel(),
        ownerId = ownerId,
        room = room,
        section = section,
        subject = subject,
        updateTime = updateTime,
    )

fun Course.toCourseDto(): CourseDto =
    CourseDto(
        alternateLink = alternateLink,
        creationTime = creationTime,
        description = description,
        enrollmentCode = enrollmentCode,
        id = id,
        owner = owner?.toUserDto(),
        name = name,
        ownerId = ownerId,
        room = room,
        section = section,
        subject = subject,
        updateTime = updateTime,
    )
