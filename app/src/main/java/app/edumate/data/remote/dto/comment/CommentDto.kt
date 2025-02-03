package app.edumate.data.remote.dto.comment

import app.edumate.data.remote.dto.user.UserDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentDto(
    @SerialName("creation_time")
    val creationTime: String? = null,
    val creator: UserDto? = null,
    @SerialName("creator_user_id")
    val creatorUserId: String? = null,
    val id: String? = null,
    val text: String? = null,
    @SerialName("update_time")
    val updateTime: String? = null,
)
