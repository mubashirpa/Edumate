package edumate.app.domain.usecase.courses

import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.data.remote.mapper.toCourse
import edumate.app.domain.model.courses.Course
import edumate.app.domain.repository.CoursesRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetCourseUseCase @Inject constructor(
    private val coursesRepository: CoursesRepository
) {
    operator fun invoke(courseId: String): Flow<Resource<Course?>> = flow {
        try {
            emit(Resource.Loading())
            val course = coursesRepository.getCourse(courseId)?.toCourse()
            emit(Resource.Success(course))
        } catch (e: Exception) {
            emit(Resource.Error(UiText.DynamicString(e.message!!)))
        }
    }
}