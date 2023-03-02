package edumate.app.domain.usecase.rooms

import edumate.app.R.string as Strings
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.data.remote.mapper.toRoomsDto
import edumate.app.domain.model.rooms.Room
import edumate.app.domain.repository.RoomsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UpdateRoomUseCase @Inject constructor(
    private val roomsRepository: RoomsRepository
) {
    operator fun invoke(roomId: String, room: Room): Flow<Resource<Boolean>> = flow {
        try {
            emit(Resource.Loading())
            roomsRepository.update(roomId, room.toRoomsDto())
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