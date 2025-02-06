package app.edumate.domain.usecase.studentSubmission

import app.edumate.R
import app.edumate.core.Result
import app.edumate.core.UiText
import app.edumate.data.mapper.toStudentSubmissionDomainModel
import app.edumate.domain.model.courseWork.CourseWorkType
import app.edumate.domain.model.studentSubmission.StudentSubmission
import app.edumate.domain.repository.AuthenticationRepository
import app.edumate.domain.repository.StudentSubmissionRepository
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GetStudentSubmissionUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val studentSubmissionRepository: StudentSubmissionRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    operator fun invoke(
        courseId: String,
        courseWorkId: String,
        courseWorkType: CourseWorkType,
    ): Flow<Result<StudentSubmission>> =
        flow {
            try {
                emit(Result.Loading())
                authenticationRepository.currentUser()?.id?.let { userId ->
                    val studentSubmission =
                        studentSubmissionRepository
                            .getStudentSubmission(
                                courseId = courseId,
                                courseWorkId = courseWorkId,
                                userId = userId,
                                courseWorkType = enumValueOf(courseWorkType.name),
                            ).toStudentSubmissionDomainModel()
                    emit(Result.Success(studentSubmission))
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
