package app.edumate.domain.usecase.announcement

import app.edumate.core.Result
import app.edumate.core.UnauthenticatedAccessException
import app.edumate.core.utils.execute
import app.edumate.data.mapper.toComment
import app.edumate.domain.model.comment.Comment
import app.edumate.domain.repository.AnnouncementRepository
import app.edumate.domain.repository.AuthenticationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class CreateAnnouncementCommentUseCase(
    private val announcementRepository: AnnouncementRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    operator fun invoke(
        courseId: String,
        announcementId: String,
        text: String,
    ): Flow<Result<Comment>> =
        execute(ioDispatcher) {
            val userId =
                authenticationRepository.currentUser()?.id ?: throw UnauthenticatedAccessException()
            announcementRepository.createComment(courseId, announcementId, userId, text).toComment()
        }
}
