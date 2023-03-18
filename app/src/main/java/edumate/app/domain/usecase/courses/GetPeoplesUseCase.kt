package edumate.app.domain.usecase.courses

import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.data.remote.mapper.toUser
import edumate.app.domain.model.User
import edumate.app.domain.repository.StudentsRepository
import edumate.app.domain.repository.TeachersRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetPeoplesUseCase @Inject constructor(
    private val teachersRepository: TeachersRepository,
    private val studentsRepository: StudentsRepository
) {
    operator fun invoke(courseId: String): Flow<Resource<List<User>>> = flow {
        try {
            emit(Resource.Loading())
            val teachers = teachersRepository.teachers(courseId).map { it.toUser() }
            val students = studentsRepository.students(courseId).map { it.toUser() }
            val peoples: MutableList<User> = mutableListOf()
            peoples.addAll(teachers)
            peoples.addAll(students)
            emit(Resource.Success(peoples))
        } catch (e: Exception) {
            emit(Resource.Error(UiText.DynamicString(e.message!!)))
        }
    }
}