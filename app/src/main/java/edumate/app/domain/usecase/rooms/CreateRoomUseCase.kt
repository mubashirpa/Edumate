package edumate.app.domain.usecase.rooms

import edumate.app.R.string as Strings
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.data.remote.mapper.toRoomsDto
import edumate.app.domain.model.rooms.Room
import edumate.app.domain.repository.PeoplesRepository
import edumate.app.domain.repository.RoomsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CreateRoomUseCase @Inject constructor(
    private val roomsRepository: RoomsRepository,
    private val peoplesRepository: PeoplesRepository
) {
    operator fun invoke(room: Room, uid: String): Flow<Resource<String>> = flow {
        var roomId: String? = null
        try {
            emit(Resource.Loading())
            roomId = roomsRepository.add(room.toRoomsDto())
            peoplesRepository.addTeacherInUser(uid, roomId)
            emit(Resource.Success(roomId))
        } catch (e: Exception) {
            val message = e.localizedMessage
            if (message != null) {
                emit(Resource.Error(UiText.DynamicString(e.localizedMessage!!), roomId))
            } else {
                emit(Resource.Error(UiText.StringResource(Strings.error_unexpected), roomId))
            }
        }
    }
}