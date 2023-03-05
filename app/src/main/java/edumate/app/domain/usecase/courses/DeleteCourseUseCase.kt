package edumate.app.domain.usecase.courses

import android.util.Log
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.domain.repository.CoursesRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DeleteCourseUseCase @Inject constructor(
    private val coursesRepository: CoursesRepository
) {
    operator fun invoke(courseId: String): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())
            coursesRepository.deleteCourse(courseId)
            Log.d(TAG, "Course $courseId deleted.")
            emit(Resource.Success(courseId))
        } catch (e: Exception) {
            emit(Resource.Error(UiText.DynamicString(e.message!!)))
        }
    }

    companion object {
        private val TAG = DeleteCourseUseCase::class.java.simpleName
    }
}