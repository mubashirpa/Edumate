package app.edumate.domain.usecase.announcement

import app.edumate.core.Constants
import app.edumate.core.Result
import app.edumate.core.UnauthenticatedAccessException
import app.edumate.core.utils.execute
import app.edumate.data.mapper.toAnnouncementDomainModel
import app.edumate.data.mapper.toAnnouncementDto
import app.edumate.domain.model.announcement.Announcement
import app.edumate.domain.model.material.Material
import app.edumate.domain.repository.AnnouncementRepository
import app.edumate.domain.repository.AuthenticationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class CreateAnnouncementUseCase(
    private val announcementRepository: AnnouncementRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    operator fun invoke(
        courseId: String,
        text: String,
        materials: List<Material>?,
        id: String = UUID.randomUUID().toString(),
    ): Flow<Result<Announcement>> =
        execute(ioDispatcher) {
            val userId =
                authenticationRepository.currentUser()?.id ?: throw UnauthenticatedAccessException()
            val announcement =
                Announcement(
                    alternateLink = "${Constants.EDUMATE_BASE_URL}c/$courseId/p/$id/details",
                    courseId = courseId,
                    creatorUserId = userId,
                    id = id,
                    materials = materials.takeIf { !it.isNullOrEmpty() },
                    text = text,
                ).toAnnouncementDto()
            announcementRepository.createAnnouncement(announcement).toAnnouncementDomainModel()
        }
}
