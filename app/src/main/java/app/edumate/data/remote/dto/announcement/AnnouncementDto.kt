package app.edumate.data.remote.dto.announcement

import app.edumate.data.remote.dto.material.MaterialDto
import app.edumate.data.remote.dto.user.UserDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnnouncementDto(
    @SerialName("alternate_link")
    val alternateLink: String? = null,
    @SerialName("course_id")
    val courseId: String? = null,
    @SerialName("creation_time")
    val creationTime: String? = null,
    val creator: UserDto? = null,
    @SerialName("creator_user_id")
    val creatorUserId: String? = null,
    val id: String? = null,
    val materials: List<MaterialDto>? = null,
    val pinned: Boolean? = null,
    val text: String? = null,
    @SerialName("total_comments")
    val totalComments: Int? = null,
    @SerialName("update_time")
    val updateTime: String? = null,
)
