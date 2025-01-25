package app.edumate.domain.repository

interface MemberRepository {
    suspend fun insertMember(
        courseId: String,
        userId: String,
    )

    suspend fun deleteMember(
        courseId: String,
        userId: String,
    )
}
