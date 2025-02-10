package app.edumate.domain.usecase.announcement

import app.edumate.core.Result
import app.edumate.core.utils.execute
import app.edumate.data.mapper.toComment
import app.edumate.domain.model.comment.Comment
import app.edumate.domain.repository.AnnouncementRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class GetAnnouncementCommentsUseCase(
    private val announcementRepository: AnnouncementRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    operator fun invoke(id: String): Flow<Result<List<Comment>>> =
        execute(ioDispatcher) {
            announcementRepository.getComments(id).map { it.toComment() }
        }
}
