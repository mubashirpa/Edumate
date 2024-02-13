package edumate.app.domain.usecase.students

import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.domain.repository.FirebaseAuthRepository
import edumate.app.domain.repository.StudentsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AddStudentUseCase
    @Inject
    constructor(
        private val studentsRepository: StudentsRepository,
        private val firebaseAuthRepository: FirebaseAuthRepository,
    ) {
        operator fun invoke(courseId: String): Flow<Result<String>> =
            flow {
                try {
                    emit(Result.Loading())
                    val userId = firebaseAuthRepository.currentUserId
                    studentsRepository.create(courseId, userId)
                    emit(Result.Success(userId))
                } catch (e: Exception) {
                    emit(Result.Error(UiText.DynamicString(e.message!!)))
                }
            }
    }
