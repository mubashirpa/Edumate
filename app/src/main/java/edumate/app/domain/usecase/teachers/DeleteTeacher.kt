package edumate.app.domain.usecase.teachers

import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.domain.repository.TeachersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteTeacher
    @Inject
    constructor(
        private val teachersRepository: TeachersRepository,
    ) {
        operator fun invoke(
            courseId: String,
            userId: String,
        ): Flow<Resource<String>> =
            flow {
                try {
                    emit(Resource.Loading())
                    teachersRepository.delete(courseId, userId)
                    emit(Resource.Success(userId))
                } catch (e: Exception) {
                    emit(Resource.Error(UiText.DynamicString(e.message!!)))
                }
            }
    }
