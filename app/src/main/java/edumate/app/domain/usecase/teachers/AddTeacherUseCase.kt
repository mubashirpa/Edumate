package edumate.app.domain.usecase.teachers

import android.util.Log
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.domain.repository.FirebaseAuthRepository
import edumate.app.domain.repository.TeachersRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AddTeacherUseCase @Inject constructor(
    private val teachersRepository: TeachersRepository,
    private val firebaseAuthRepository: FirebaseAuthRepository
) {
    operator fun invoke(courseId: String): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())
            val teacherId =
                teachersRepository.addTeacher(courseId, firebaseAuthRepository.currentUserId)
            Log.d(
                TAG,
                "UserProfile $teacherId was added as teacher to the course with ID $courseId."
            )
            emit(Resource.Success(teacherId))
        } catch (e: Exception) {
            emit(Resource.Error(UiText.DynamicString(e.message!!)))
        }
    }

    companion object {
        private val TAG = AddTeacherUseCase::class.java.simpleName
    }
}