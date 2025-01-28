package app.edumate.data.mapper

import app.edumate.data.remote.dto.courses.CourseDto
import app.edumate.data.remote.dto.courses.CourseWithMembersDto
import app.edumate.data.remote.dto.courses.CoursesDto
import app.edumate.domain.model.course.Course
import app.edumate.domain.model.course.CourseWithMembers
import app.edumate.domain.model.course.Courses
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
        owner = owner?.toUserDto(),
        name = name,
        ownerId = ownerId,
        room = room,
        section = section,
        subject = subject,
        updateTime = updateTime,
    )

fun CoursesDto.toCoursesDomainModel(): Courses =
    Courses(
        course = course?.toCourseDomainModel(),
        role = role?.let { enumValueOf<UserRole>(it.name) },
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
