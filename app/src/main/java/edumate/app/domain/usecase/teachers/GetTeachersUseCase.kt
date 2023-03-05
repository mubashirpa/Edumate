package edumate.app.domain.usecase.teachers

import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.data.remote.mapper.toUser
import edumate.app.domain.model.User
import edumate.app.domain.repository.TeachersRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetTeachersUseCase @Inject constructor(
    private val teachersRepository: TeachersRepository
) {
    operator fun invoke(courseId: String): Flow<Resource<List<User>>> = flow {
        try {
            emit(Resource.Loading())
            val teachers = teachersRepository.teachers(courseId).map { it.toUser() }
            emit(Resource.Success(teachers))
        } catch (e: Exception) {
            emit(Resource.Error(UiText.DynamicString(e.message!!)))
        }
    }
}