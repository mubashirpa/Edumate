package edumate.app.domain.usecase.courses

import android.util.Log
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.data.remote.mapper.toCoursesDto
import edumate.app.domain.model.Course
import edumate.app.domain.repository.CoursesRepository
import edumate.app.domain.repository.FirebaseAuthRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CreateCourseUseCase @Inject constructor(
    private val coursesRepository: CoursesRepository,
    private val firebaseAuthRepository: FirebaseAuthRepository
) {
    operator fun invoke(course: Course): Flow<Resource<String>> = flow {
        var courseId: String? = null
        try {
            emit(Resource.Loading())
            courseId = coursesRepository.createCourse(
                course.toCoursesDto(),
                firebaseAuthRepository.currentUserId
            )
            Log.d(TAG, "Course created: ${course.name} ($courseId).")
            emit(Resource.Success(courseId))
        } catch (e: Exception) {
            // If course created and error occurs while adding $courseId
            // in users/$uid/teaching array, delete the created course using $courseId
            emit(Resource.Error(UiText.DynamicString(e.message!!), courseId))
        }
    }

    companion object {
        private val TAG = CreateCourseUseCase::class.java.simpleName
    }
}