package app.edumate.domain.repository

import app.edumate.data.remote.dto.user.UsersDto

interface MemberRepository {
    suspend fun getMembers(courseId: String): List<UsersDto>

    suspend fun insertMember(
        courseId: String,
        userId: String,
    )

    suspend fun deleteMember(
        courseId: String,
        userId: String,
    )
}
