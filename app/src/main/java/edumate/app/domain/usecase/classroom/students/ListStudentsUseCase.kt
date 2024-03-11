package edumate.app.domain.usecase.classroom.students

import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.data.mapper.toStudentDomainModel
import edumate.app.domain.model.classroom.students.Student
import edumate.app.domain.repository.AuthenticationRepository
import edumate.app.domain.repository.StudentsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ListStudentsUseCase
    @Inject
    constructor(
        private val authenticationRepository: AuthenticationRepository,
        private val studentsRepository: StudentsRepository,
    ) {
        operator fun invoke(
            courseId: String,
            pageSize: Int? = 30,
            page: Int? = null,
        ): Flow<Result<List<Student>>> =
            flow {
                try {
                    emit(Result.Loading())
                    val idToken = authenticationRepository.getIdToken()
                    val students =
                        studentsRepository.list(
                            idToken,
                            courseId,
                            pageSize,
                            page,
                        ).students?.map { it.toStudentDomainModel() }
                    emit(Result.Success(students))
                } catch (e: Exception) {
                    emit(Result.Error(UiText.DynamicString(e.message!!)))
                }
            }
    }
