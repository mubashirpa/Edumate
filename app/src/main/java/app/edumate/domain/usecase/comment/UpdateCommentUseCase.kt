package app.edumate.domain.usecase.comment

import app.edumate.core.Result
import app.edumate.core.utils.execute
import app.edumate.data.mapper.toComment
import app.edumate.domain.model.comment.Comment
import app.edumate.domain.repository.CommentRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class UpdateCommentUseCase(
    private val commentRepository: CommentRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    operator fun invoke(
        id: String,
        text: String,
    ): Flow<Result<Comment>> =
        execute(ioDispatcher) {
            commentRepository.updateComment(id, text).toComment()
        }
}
