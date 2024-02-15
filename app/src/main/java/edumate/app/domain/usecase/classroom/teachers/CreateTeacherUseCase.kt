package edumate.app.domain.usecase.classroom.teachers

import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.data.mapper.toTeacher
import edumate.app.data.mapper.toTeacherDomainModel
import edumate.app.domain.model.classroom.teachers.Teacher
import edumate.app.domain.repository.TeachersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreateTeacherUseCase
    @Inject
    constructor(
        private val teachersRepository: TeachersRepository,
    ) {
        operator fun invoke(
            courseId: String,
            teacher: Teacher,
        ): Flow<Result<Teacher>> =
            flow {
                try {
                    emit(Result.Loading())
                    val teacherResponse =
                        teachersRepository.create(courseId, teacher.toTeacher()).toTeacherDomainModel()
                    emit(Result.Success(teacherResponse))
                } catch (e: Exception) {
                    emit(Result.Error(UiText.DynamicString(e.message!!)))
                }
            }
    }
