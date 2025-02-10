package app.edumate.domain.usecase.announcement

import app.edumate.core.Result
import app.edumate.core.utils.execute
import app.edumate.domain.repository.AnnouncementRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class DeleteAnnouncementUseCase(
    private val announcementRepository: AnnouncementRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    operator fun invoke(id: String): Flow<Result<Boolean>> =
        execute(ioDispatcher) {
            announcementRepository.deleteAnnouncement(id)
            true
        }
}
