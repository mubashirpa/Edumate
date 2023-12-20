package edumate.app.domain.usecase.teachers

import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.domain.repository.FirebaseAuthRepository
import edumate.app.domain.repository.TeachersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AddTeacher
    @Inject
    constructor(
        private val teachersRepository: TeachersRepository,
        private val firebaseAuthRepository: FirebaseAuthRepository,
    ) {
        operator fun invoke(courseId: String): Flow<Resource<String>> =
            flow {
                try {
                    emit(Resource.Loading())
                    val userId = firebaseAuthRepository.currentUserId
                    teachersRepository.create(courseId, userId)
                    emit(Resource.Success(userId))
                } catch (e: Exception) {
                    emit(Resource.Error(UiText.DynamicString(e.message!!)))
                }
            }
    }
