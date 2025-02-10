package app.edumate.domain.usecase.course

import app.edumate.core.Result
import app.edumate.core.UnauthenticatedAccessException
import app.edumate.core.utils.execute
import app.edumate.data.mapper.toCourseDomainModel
import app.edumate.domain.model.course.Course
import app.edumate.domain.repository.AuthenticationRepository
import app.edumate.domain.repository.CourseRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class GetCoursesUseCase(
    private val courseRepository: CourseRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    operator fun invoke(): Flow<Result<List<Course>>> =
        execute(ioDispatcher) {
            val userId =
                authenticationRepository.currentUser()?.id ?: throw UnauthenticatedAccessException()
            courseRepository
                .getCourses(userId)
                .map { it.toCourseDomainModel() }
        }
}
