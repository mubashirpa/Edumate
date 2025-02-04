package app.edumate.domain.repository

import app.edumate.data.remote.dto.member.UserRoleDto
import app.edumate.data.remote.dto.user.UsersDto

interface MemberRepository {
    suspend fun getMembers(courseId: String): List<UsersDto>

    suspend fun insertMember(
        courseId: String,
        userId: String,
    )

    suspend fun updateMember(
        courseId: String,
        userId: String,
        role: UserRoleDto,
    )

    suspend fun deleteMember(
        courseId: String,
        userId: String,
    )
}
