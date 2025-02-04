package app.edumate.domain.usecase.course

import app.edumate.R
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

class GetCoursesUseCase(
    private val courseRepository: CourseRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    operator fun invoke(): Flow<Result<List<Course>>> =
        flow {
            try {
                emit(Result.Loading())
                authenticationRepository.currentUser()?.id?.let { userId ->
                    val courses =
                        courseRepository
                            .getCourses(userId)
                            .map { it.toCourseDomainModel() }
                    emit(Result.Success(courses))
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
