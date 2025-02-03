package app.edumate.domain.repository

import app.edumate.data.remote.dto.comment.CommentsDto

interface CommentRepository {
    suspend fun updateComment(
        id: String,
        text: String,
    ): CommentsDto

    suspend fun deleteComment(id: String): CommentsDto
}
