package edumate.app.domain.usecase.student_submission

import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.data.remote.mapper.toUser
import edumate.app.domain.model.User
import edumate.app.domain.repository.StudentsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetStudentsUseCase @Inject constructor(
    private val studentsRepository: StudentsRepository
) {
    operator fun invoke(courseId: String): Flow<Resource<List<User>>> = flow {
        try {
            emit(Resource.Loading())
            val students = studentsRepository.students(courseId).map { it.toUser() }
            emit(Resource.Success(students))
        } catch (e: Exception) {
            emit(Resource.Error(UiText.DynamicString(e.message!!)))
        }
    }
}