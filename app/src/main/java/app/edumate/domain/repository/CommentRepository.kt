package app.edumate.domain.repository

import app.edumate.data.remote.dto.comment.CommentsDto

interface CommentRepository {
    suspend fun updateComment(
        commentId: String,
        text: String,
    ): CommentsDto

    suspend fun deleteComment(commentId: String): CommentsDto
}
