package edumate.app.domain.usecase.students

import edumate.app.core.UiText
import edumate.app.core.utils.ResourceNew
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
        ): Flow<ResourceNew<String>> =
            flow {
                try {
                    emit(ResourceNew.Loading())
                    studentsRepository.delete(courseId, userId)
                    emit(ResourceNew.Success(userId))
                } catch (e: Exception) {
                    emit(ResourceNew.Error(UiText.DynamicString(e.message!!)))
                }
            }
    }
