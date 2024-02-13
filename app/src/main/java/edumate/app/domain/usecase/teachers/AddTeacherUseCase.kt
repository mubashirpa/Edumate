package edumate.app.domain.usecase.teachers

import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.domain.repository.FirebaseAuthRepository
import edumate.app.domain.repository.TeachersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AddTeacherUseCase
    @Inject
    constructor(
        private val teachersRepository: TeachersRepository,
        private val firebaseAuthRepository: FirebaseAuthRepository,
    ) {
        operator fun invoke(courseId: String): Flow<Result<String>> =
            flow {
                try {
                    emit(Result.Loading())
                    val userId = firebaseAuthRepository.currentUserId
                    teachersRepository.create(courseId, userId)
                    emit(Result.Success(userId))
                } catch (e: Exception) {
                    emit(Result.Error(UiText.DynamicString(e.message!!)))
                }
            }
    }
