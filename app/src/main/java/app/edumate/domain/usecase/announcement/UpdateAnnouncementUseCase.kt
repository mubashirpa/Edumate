package app.edumate.domain.usecase.announcement

import app.edumate.core.Result
import app.edumate.core.utils.execute
import app.edumate.data.mapper.toAnnouncementDomainModel
import app.edumate.data.mapper.toMaterialDto
import app.edumate.domain.model.announcement.Announcement
import app.edumate.domain.model.material.Material
import app.edumate.domain.repository.AnnouncementRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class UpdateAnnouncementUseCase(
    private val announcementRepository: AnnouncementRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    operator fun invoke(
        id: String,
        text: String? = null,
        materials: List<Material>? = null,
        pinned: Boolean? = null,
    ): Flow<Result<Announcement>> =
        execute(ioDispatcher) {
            val now: Instant = Clock.System.now()
            val updateTime = now.toLocalDateTime(TimeZone.UTC)

            announcementRepository
                .updateAnnouncement(
                    id = id,
                    text = text,
                    materials = materials?.map { it.toMaterialDto() },
                    pinned = pinned,
                    updateTime = updateTime,
                ).toAnnouncementDomainModel()
        }
}
