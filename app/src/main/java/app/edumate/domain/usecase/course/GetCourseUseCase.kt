package app.edumate.domain.usecase.course

import app.edumate.core.Result
import app.edumate.core.utils.execute
import app.edumate.data.mapper.toCourseDomainModel
import app.edumate.domain.model.course.Course
import app.edumate.domain.repository.CourseRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class GetCourseUseCase(
    private val courseRepository: CourseRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    operator fun invoke(id: String): Flow<Result<Course>> =
        execute(ioDispatcher) {
            courseRepository.getCourse(id).toCourseDomainModel()
        }
}
