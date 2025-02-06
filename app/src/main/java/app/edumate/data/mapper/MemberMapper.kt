package app.edumate.data.mapper

import app.edumate.data.remote.dto.member.MemberDto
import app.edumate.domain.model.member.Member
import app.edumate.domain.model.member.UserRole

fun MemberDto.toMemberDomainModel(): Member =
    Member(
        courseId = courseId,
        joinedAt = joinedAt,
        role = role?.let { enumValueOf<UserRole>(it.name) },
        userId = userId,
    )
