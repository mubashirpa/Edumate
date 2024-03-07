package edumate.app.domain.usecase.classroom.teachers

import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.domain.repository.AuthenticationRepository
import edumate.app.domain.repository.TeachersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteTeacherUseCase
    @Inject
    constructor(
        private val authenticationRepository: AuthenticationRepository,
        private val teachersRepository: TeachersRepository,
    ) {
        operator fun invoke(
            courseId: String,
            userId: String,
        ): Flow<Result<String>> =
            flow {
                try {
                    emit(Result.Loading())
                    val idToken = authenticationRepository.getIdToken()
                    teachersRepository.delete(idToken, courseId, userId)
                    emit(Result.Success(userId))
                } catch (e: Exception) {
                    emit(Result.Error(UiText.DynamicString(e.message!!)))
                }
            }
    }
