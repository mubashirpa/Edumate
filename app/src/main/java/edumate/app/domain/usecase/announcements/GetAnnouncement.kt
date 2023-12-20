package edumate.app.domain.usecase.announcements

import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.data.remote.mapper.toAnnouncement
import edumate.app.domain.model.announcements.Announcement
import edumate.app.domain.repository.AnnouncementsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import edumate.app.R.string as Strings

class GetAnnouncement
    @Inject
    constructor(
        private val announcementsRepository: AnnouncementsRepository,
    ) {
        operator fun invoke(
            courseId: String,
            id: String,
        ): Flow<Resource<Announcement?>> =
            flow {
                try {
                    emit(Resource.Loading())
                    val announcement = announcementsRepository.get(courseId, id)?.toAnnouncement()
                    emit(Resource.Success(announcement))
                } catch (e: Exception) {
                    emit(
                        Resource.Error(
                            UiText.StringResource(
                                Strings.cannot_retrieve_announcement_at_this_time_please_try_again_later,
                            ),
                        ),
                    )
                }
            }
    }
