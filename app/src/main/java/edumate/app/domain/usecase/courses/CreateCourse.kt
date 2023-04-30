package edumate.app.domain.usecase.courses

import edumate.app.R.string as Strings
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.data.remote.mapper.toCourse
import edumate.app.data.remote.mapper.toCoursesDto
import edumate.app.domain.model.courses.Course
import edumate.app.domain.repository.CoursesRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CreateCourse @Inject constructor(
    private val coursesRepository: CoursesRepository
) {
    operator fun invoke(course: Course): Flow<Resource<Course?>> = flow {
        var updatedCourse: Course? = null
        try {
            emit(Resource.Loading())
            updatedCourse = coursesRepository.create(course.toCoursesDto())?.toCourse()
            emit(Resource.Success(updatedCourse))
        } catch (e: Exception) {
            // If course created and error occurs while adding $courseId
            // in users/$uid/teaching array, delete the created course using $courseId.
            emit(
                Resource.Error(
                    UiText.StringResource(Strings.unable_to_create_course),
                    updatedCourse
                )
            )
        }
    }
}