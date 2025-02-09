package app.edumate.domain.usecase.studentSubmission

import app.edumate.R
import app.edumate.core.Result
import app.edumate.core.UiText
import app.edumate.data.mapper.toStudentSubmissionDomainModel
import app.edumate.domain.model.studentSubmission.StudentSubmission
import app.edumate.domain.repository.AuthenticationRepository
import app.edumate.domain.repository.StudentSubmissionRepository
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart

class GetStudentSubmissionUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val studentSubmissionRepository: StudentSubmissionRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    operator fun invoke(
        courseId: String,
        courseWorkId: String,
    ): Flow<Result<StudentSubmission>> =
        execute {
            val userId =
                authenticationRepository.currentUser()?.id ?: throw UserNotLoggedInException()
            studentSubmissionRepository
                .getStudentSubmission(
                    courseId = courseId,
                    courseWorkId = courseWorkId,
                    userId = userId,
                ).toStudentSubmissionDomainModel()
        }

    operator fun invoke(
        courseId: String,
        courseWorkId: String,
        userId: String,
    ): Flow<Result<StudentSubmission>> =
        execute {
            studentSubmissionRepository
                .getStudentSubmission(
                    courseId = courseId,
                    courseWorkId = courseWorkId,
                    userId = userId,
                ).toStudentSubmissionDomainModel()
        }

    private fun execute(block: suspend () -> StudentSubmission): Flow<Result<StudentSubmission>> =
        flow<Result<StudentSubmission>> {
            val result = block()
            emit(Result.Success(result))
        }.onStart {
            emit(Result.Loading())
        }.catch { throwable ->
            emit(Result.Error(throwable.toUiText()))
        }.flowOn(ioDispatcher)

    private fun Throwable.toUiText(): UiText =
        when (this) {
            is RestException -> UiText.DynamicString(message.toString())
            is HttpRequestTimeoutException -> UiText.StringResource(R.string.error_timeout_exception)
            is HttpRequestException -> UiText.StringResource(R.string.error_network_exception)
            is UserNotLoggedInException -> UiText.StringResource(R.string.error_unexpected)
            else -> UiText.StringResource(R.string.error_unknown)
        }
}

class UserNotLoggedInException : Exception()
