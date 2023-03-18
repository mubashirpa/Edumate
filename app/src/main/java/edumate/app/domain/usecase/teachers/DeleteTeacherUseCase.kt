package edumate.app.domain.usecase.teachers

import android.util.Log
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.domain.repository.TeachersRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DeleteTeacherUseCase @Inject constructor(
    private val teachersRepository: TeachersRepository
) {
    operator fun invoke(courseId: String, uid: String): Flow<Resource<Boolean>> = flow {
        try {
            emit(Resource.Loading())
            teachersRepository.deleteTeacher(courseId, uid)
            Log.d(TAG, "The teacher $uid was deleted from the course with ID $courseId.")
            emit(Resource.Success(true))
        } catch (e: Exception) {
            emit(Resource.Error(UiText.DynamicString(e.message!!)))
        }
    }

    companion object {
        private val TAG = DeleteTeacherUseCase::class.java.simpleName
    }
}