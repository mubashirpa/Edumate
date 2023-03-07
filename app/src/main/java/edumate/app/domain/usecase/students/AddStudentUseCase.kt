package edumate.app.domain.usecase.students

import android.util.Log
import edumate.app.R.string as Strings
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.domain.repository.FirebaseAuthRepository
import edumate.app.domain.repository.StudentsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AddStudentUseCase @Inject constructor(
    private val studentsRepository: StudentsRepository,
    private val firebaseAuthRepository: FirebaseAuthRepository
) {
    operator fun invoke(courseId: String): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())
            val studentId =
                studentsRepository.addStudent(courseId, firebaseAuthRepository.currentUserId)
            Log.d(TAG, "User $studentId was enrolled as student in the course with ID $courseId.")
            emit(Resource.Success(studentId))
        } catch (e: Exception) {
            emit(Resource.Error(UiText.StringResource(Strings.error_course_not_found)))
        }
    }

    companion object {
        private val TAG = AddStudentUseCase::class.java.simpleName
    }
}