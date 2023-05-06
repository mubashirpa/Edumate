package edumate.app.domain.usecase.announcements

import edumate.app.R.string as Strings
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.domain.repository.AnnouncementsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DeleteAnnouncement @Inject constructor(
    private val announcementsRepository: AnnouncementsRepository
) {
    operator fun invoke(courseId: String, id: String): Flow<Resource<String>> =
        flow {
            try {
                emit(Resource.Loading())
                announcementsRepository.delete(courseId, id)
                emit(Resource.Success(id))
            } catch (e: Exception) {
                emit(Resource.Error(UiText.StringResource(Strings.unable_to_delete_announcement)))
            }
        }
}