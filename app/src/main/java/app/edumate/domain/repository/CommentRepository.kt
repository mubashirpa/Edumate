package app.edumate.domain.repository

import app.edumate.data.remote.dto.comment.CommentDto

interface CommentRepository {
    suspend fun updateComment(
        id: String,
        text: String,
    ): CommentDto

    suspend fun deleteComment(id: String): CommentDto
}
