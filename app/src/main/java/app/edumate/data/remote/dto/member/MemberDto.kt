package app.edumate.data.remote.dto.member

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberDto(
    @SerialName("course_id")
    val courseId: String? = null,
    @SerialName("joined_at")
    val joinedAt: String? = null,
    val role: UserRoleDto? = null,
    @SerialName("user_id")
    val userId: String? = null,
)
