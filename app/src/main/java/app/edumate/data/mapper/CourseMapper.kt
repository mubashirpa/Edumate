package app.edumate.data.mapper

import app.edumate.data.remote.dto.course.CourseDto
import app.edumate.data.remote.dto.course.CourseWithMembersDto
import app.edumate.data.remote.dto.course.CoursesDto
import app.edumate.domain.model.course.Course
import app.edumate.domain.model.course.CourseWithMembers
import app.edumate.domain.model.member.UserRole

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
        name = name,
        owner = owner?.toUserDto(),
        ownerId = ownerId,
        room = room,
        section = section,
        subject = subject,
        updateTime = updateTime,
    )

fun CoursesDto.toCourseDomainModel(): Course =
    Course(
        alternateLink = course?.alternateLink,
        creationTime = course?.creationTime,
        description = course?.description,
        enrollmentCode = course?.enrollmentCode,
        id = course?.id,
        name = course?.name,
        owner = course?.owner?.toUserDomainModel(),
        ownerId = course?.ownerId,
        role = role?.let { enumValueOf<UserRole>(it.name) },
        room = course?.room,
        section = course?.section,
        subject = course?.subject,
        updateTime = course?.updateTime,
    )

fun CourseWithMembersDto.toCourseWithMembersDomainModel(): CourseWithMembers =
    CourseWithMembers(
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
        members = members?.map { it.toMemberDomainModel() },
    )
