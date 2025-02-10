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

class UpdateAnnouncementUseCase(
    private val announcementRepository: AnnouncementRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    operator fun invoke(
        id: String,
        text: String,
        materials: List<Material>?,
    ): Flow<Result<Announcement>> =
        execute(ioDispatcher) {
            announcementRepository
                .updateAnnouncement(
                    id = id,
                    text = text,
                    materials =
                        materials
                            ?.map { it.toMaterialDto() }
                            .takeIf { !it.isNullOrEmpty() },
                ).toAnnouncementDomainModel()
        }
}
