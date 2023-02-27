package edumate.app.domain.usecase.rooms

import edumate.app.R.string as Strings
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.data.remote.mapper.toRoom
import edumate.app.domain.model.Room
import edumate.app.domain.repository.RoomsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetRoomsUseCase @Inject constructor(
    private val repository: RoomsRepository
) {
    operator fun invoke(): Flow<Resource<List<Room>>> = flow {
        try {
            emit(Resource.Loading())
            val rooms = repository.rooms().map { it.toRoom() }
            emit(Resource.Success(rooms))
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