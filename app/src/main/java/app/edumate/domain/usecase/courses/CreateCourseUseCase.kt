package app.edumate.domain.usecase.courses

import app.edumate.R
import app.edumate.core.Constants
import app.edumate.core.Result
import app.edumate.core.UiText
import app.edumate.data.mapper.toCourseDomainModel
import app.edumate.domain.model.course.Course
import app.edumate.domain.repository.AuthenticationRepository
import app.edumate.domain.repository.CourseRepository
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
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
        flow {
            try {
                emit(Result.Loading())
                authenticationRepository.currentUser()?.id?.let { userId ->
                    val id = UUID.randomUUID().toString()
                    val course =
                        Course(
                            alternateLink = "${Constants.EDUMATE_BASE_URL}course/$id",
                            enrollmentCode = id,
                            id = id,
                            name = name,
                            ownerId = userId,
                            room = room,
                            section = section,
                            subject = subject,
                        )
                    val result = courseRepository.createCourse(course).toCourseDomainModel()
                    emit(Result.Success(result))
                } ?: emit(Result.Error(UiText.StringResource(R.string.error_unexpected)))
            } catch (_: RestException) {
                emit(Result.Error(UiText.StringResource(R.string.error_unexpected)))
            } catch (_: HttpRequestTimeoutException) {
                emit(Result.Error(UiText.StringResource(R.string.error_timeout_exception)))
            } catch (_: HttpRequestException) {
                emit(Result.Error(UiText.StringResource(R.string.error_network_exception)))
            } catch (_: Exception) {
                emit(Result.Error(UiText.StringResource(R.string.error_unknown)))
            }
        }.flowOn(ioDispatcher)
}
