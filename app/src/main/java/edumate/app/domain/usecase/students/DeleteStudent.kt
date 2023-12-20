package edumate.app.domain.usecase.students

import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.domain.repository.StudentsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteStudent
    @Inject
    constructor(
        private val studentsRepository: StudentsRepository,
    ) {
        operator fun invoke(
            courseId: String,
            userId: String,
        ): Flow<Resource<String>> =
            flow {
                try {
                    emit(Resource.Loading())
                    studentsRepository.delete(courseId, userId)
                    emit(Resource.Success(userId))
                } catch (e: Exception) {
                    emit(Resource.Error(UiText.DynamicString(e.message!!)))
                }
            }
    }
