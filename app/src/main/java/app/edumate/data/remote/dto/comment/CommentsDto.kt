package app.edumate.data.remote.dto.comment

import kotlinx.serialization.Serializable

@Serializable
data class CommentsDto(
    val comment: CommentDto? = null,
)
