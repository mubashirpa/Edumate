package app.edumate.data.remote.dto.courses

import app.edumate.data.remote.dto.member.MemberDto
import app.edumate.data.remote.dto.users.UserDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CourseWithMembersDto(
    @SerialName("alternate_link")
    val alternateLink: String? = null,
    @SerialName("creation_time")
    val creationTime: String? = null,
    val description: String? = null,
    @SerialName("enrollment_code")
    val enrollmentCode: String? = null,
    val id: String? = null,
    val name: String? = null,
    val owner: UserDto? = null,
    @SerialName("owner_id")
    val ownerId: String? = null,
    val room: String? = null,
    val section: String? = null,
    val subject: String? = null,
    @SerialName("update_time")
    val updateTime: String? = null,
    val members: List<MemberDto>? = null,
)
