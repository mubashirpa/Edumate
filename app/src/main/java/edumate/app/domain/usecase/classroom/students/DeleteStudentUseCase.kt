package edumate.app.domain.usecase.classroom.students

import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.domain.repository.AuthenticationRepository
import edumate.app.domain.repository.StudentsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteStudentUseCase
    @Inject
    constructor(
        private val authenticationRepository: AuthenticationRepository,
        private val studentsRepository: StudentsRepository,
    ) {
        operator fun invoke(
            courseId: String,
            userId: String,
        ): Flow<Result<String>> =
            flow {
                try {
                    emit(Result.Loading())
                    val idToken = authenticationRepository.getIdToken()
                    studentsRepository.delete(idToken, courseId, userId)
                    emit(Result.Success(userId))
                } catch (e: Exception) {
                    emit(Result.Error(UiText.DynamicString(e.message!!)))
                }
            }
    }
