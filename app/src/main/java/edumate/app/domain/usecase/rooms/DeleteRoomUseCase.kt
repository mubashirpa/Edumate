package edumate.app.domain.usecase.rooms

import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.domain.repository.RoomsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import edumate.app.R.string as Strings

class DeleteRoomUseCase @Inject constructor(
    private val roomsRepository: RoomsRepository
) {
    operator fun invoke(roomId: String): Flow<Resource<Boolean>> = flow {
        try {
            emit(Resource.Loading())
            roomsRepository.delete(roomId)
            emit(Resource.Success(true))
        } catch (e: Exception) {
            val message = e.localizedMessage
            if (message != null) {
                emit(Resource.Error(UiText.DynamicString(e.localizedMessage!!)))
            } else {
                emit(Resource.Error(UiText.StringResource(Strings.error_unexpected)))
            }
        }
    }
}