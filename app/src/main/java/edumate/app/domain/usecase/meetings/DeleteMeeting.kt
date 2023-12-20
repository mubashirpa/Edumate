package edumate.app.domain.usecase.meetings

import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.domain.repository.MeetingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import edumate.app.R.string as Strings

class DeleteMeeting
    @Inject
    constructor(
        private val meetingsRepository: MeetingsRepository,
    ) {
        operator fun invoke(
            courseId: String,
            id: String,
        ): Flow<Resource<String>> =
            flow {
                try {
                    emit(Resource.Loading())
                    meetingsRepository.delete(courseId, id)
                    emit(Resource.Success(id))
                } catch (e: Exception) {
                    emit(
                        Resource.Error(UiText.StringResource(Strings.unable_to_delete_meeting)),
                    )
                }
            }
    }
