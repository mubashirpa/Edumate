package app.edumate.domain.usecase.comment

import app.edumate.core.Result
import app.edumate.core.utils.execute
import app.edumate.domain.repository.CommentRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class DeleteCommentUseCase(
    private val commentRepository: CommentRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    operator fun invoke(id: String): Flow<Result<Boolean>> =
        execute(ioDispatcher) {
            commentRepository.deleteComment(id)
            true
        }
}
