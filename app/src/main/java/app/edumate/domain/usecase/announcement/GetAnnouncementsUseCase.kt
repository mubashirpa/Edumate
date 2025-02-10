package app.edumate.domain.usecase.announcement

import app.edumate.core.Result
import app.edumate.core.utils.execute
import app.edumate.data.mapper.toAnnouncementDomainModel
import app.edumate.domain.model.announcement.Announcement
import app.edumate.domain.repository.AnnouncementRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class GetAnnouncementsUseCase(
    private val announcementRepository: AnnouncementRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    operator fun invoke(courseId: String): Flow<Result<List<Announcement>>> =
        execute(ioDispatcher) {
            announcementRepository.getAnnouncements(courseId).map {
                it.toAnnouncementDomainModel()
            }
        }
}
