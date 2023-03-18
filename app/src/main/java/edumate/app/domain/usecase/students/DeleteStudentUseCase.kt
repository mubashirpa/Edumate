package edumate.app.domain.usecase.students

import android.util.Log
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.domain.repository.StudentsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DeleteStudentUseCase @Inject constructor(
    private val studentsRepository: StudentsRepository
) {
    operator fun invoke(courseId: String, uid: String): Flow<Resource<Boolean>> = flow {
        try {
            emit(Resource.Loading())
            studentsRepository.deleteStudent(courseId, uid)
            Log.d(TAG, "The student $uid was deleted from the course with ID $courseId.")
            emit(Resource.Success(true))
        } catch (e: Exception) {
            emit(Resource.Error(UiText.DynamicString(e.message!!)))
        }
    }

    companion object {
        private val TAG = DeleteStudentUseCase::class.java.simpleName
    }
}