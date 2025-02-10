package app.edumate.domain.usecase.course

import app.edumate.core.Constants
import app.edumate.core.Result
import app.edumate.core.UnauthenticatedAccessException
import app.edumate.core.utils.CryptographyUtils
import app.edumate.core.utils.execute
import app.edumate.data.mapper.toCourseDomainModel
import app.edumate.data.mapper.toCourseDto
import app.edumate.domain.model.course.Course
import app.edumate.domain.repository.AuthenticationRepository
import app.edumate.domain.repository.CourseRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class CreateCourseUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val courseRepository: CourseRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    operator fun invoke(
        name: String,
        room: String?,
        section: String?,
        subject: String?,
    ): Flow<Result<Course>> =
        execute(ioDispatcher) {
            val userId =
                authenticationRepository.currentUser()?.id ?: throw UnauthenticatedAccessException()
            val id = UUID.randomUUID().toString()
            val enrollmentCode = CryptographyUtils.generateShortHash(name, id)
            val course =
                Course(
                    alternateLink = "${Constants.EDUMATE_BASE_URL}course/$id",
                    enrollmentCode = enrollmentCode,
                    id = id,
                    name = name,
                    ownerId = userId,
                    room = room,
                    section = section,
                    subject = subject,
                ).toCourseDto()
            courseRepository.createCourse(course).toCourseDomainModel()
        }
}
