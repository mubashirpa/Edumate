package edumate.app.domain.usecase.classroom.announcements

import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.domain.repository.AnnouncementsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import edumate.app.R.string as Strings

class DeleteAnnouncementUseCase
    @Inject
    constructor(
        private val announcementsRepository: AnnouncementsRepository,
    ) {
        operator fun invoke(
            courseId: String,
            id: String,
        ): Flow<Result<String>> =
            flow {
                try {
                    emit(Result.Loading())
                    announcementsRepository.delete(courseId, id)
                    emit(Result.Success(id))
                } catch (e: Exception) {
                    emit(Result.Error(UiText.StringResource(Strings.unable_to_delete_announcement)))
                }
            }
    }
