package edumate.app.domain.usecase.classroom.students

import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.data.mapper.toStudent
import edumate.app.data.mapper.toStudentDomainModel
import edumate.app.domain.model.classroom.students.Student
import edumate.app.domain.repository.StudentsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreateStudentUseCase
    @Inject
    constructor(
        private val studentsRepository: StudentsRepository,
    ) {
        operator fun invoke(
            courseId: String,
            enrollmentCode: String? = null,
            student: Student,
        ): Flow<Result<Student>> =
            flow {
                try {
                    emit(Result.Loading())
                    val studentResponse =
                        studentsRepository.create(courseId, enrollmentCode, student.toStudent())
                            .toStudentDomainModel()
                    emit(Result.Success(studentResponse))
                } catch (e: Exception) {
                    emit(Result.Error(UiText.DynamicString(e.message!!)))
                }
            }
    }
