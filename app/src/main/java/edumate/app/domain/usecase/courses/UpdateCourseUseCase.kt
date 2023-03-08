package edumate.app.domain.usecase.courses

import android.util.Log
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.data.remote.mapper.toCoursesDto
import edumate.app.domain.model.Course
import edumate.app.domain.repository.CoursesRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UpdateCourseUseCase @Inject constructor(
    private val coursesRepository: CoursesRepository
) {
    operator fun invoke(course: Course): Flow<Resource<String>> = flow {
        val courseId = course.id!!
        try {
            emit(Resource.Loading())
            coursesRepository.updateCourse(courseId, course.toCoursesDto())
            Log.d(TAG, "Course '${course.name}' updated.")
            emit(Resource.Success(courseId))
        } catch (e: Exception) {
            emit(Resource.Error(UiText.DynamicString(e.message!!)))
        }
    }

    companion object {
        private val TAG = UpdateCourseUseCase::class.java.simpleName
    }
}