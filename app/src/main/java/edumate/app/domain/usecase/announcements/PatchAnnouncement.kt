package edumate.app.domain.usecase.announcements

import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.data.remote.mapper.toAnnouncement
import edumate.app.data.remote.mapper.toAnnouncementDto
import edumate.app.domain.model.announcements.Announcement
import edumate.app.domain.repository.AnnouncementsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import edumate.app.R.string as Strings

class PatchAnnouncement
    @Inject
    constructor(
        private val announcementsRepository: AnnouncementsRepository,
    ) {
        operator fun invoke(
            courseId: String,
            id: String,
            announcement: Announcement,
        ): Flow<Resource<Announcement?>> =
            flow {
                try {
                    emit(Resource.Loading())
                    val announcementResponse =
                        announcementsRepository.patch(courseId, id, announcement.toAnnouncementDto())
                            ?.toAnnouncement()
                    emit(Resource.Success(announcementResponse))
                } catch (e: Exception) {
                    emit(Resource.Error(UiText.StringResource(Strings.unable_to_update_announcement)))
                }
            }
    }
