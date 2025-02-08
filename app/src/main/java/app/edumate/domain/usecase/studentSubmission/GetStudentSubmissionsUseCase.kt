package app.edumate.domain.usecase.studentSubmission

import app.edumate.R
import app.edumate.core.Result
import app.edumate.core.UiText
import app.edumate.data.mapper.toStudentSubmissionListDomainModel
import app.edumate.domain.model.studentSubmission.StudentSubmissionList
import app.edumate.domain.repository.StudentSubmissionRepository
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GetStudentSubmissionsUseCase(
    private val studentSubmissionRepository: StudentSubmissionRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    operator fun invoke(
        courseId: String,
        courseWorkId: String,
    ): Flow<Result<List<StudentSubmissionList>>> =
        flow {
            try {
                emit(Result.Loading())
                val studentSubmissions =
                    studentSubmissionRepository
                        .getStudentSubmissions(
                            courseId = courseId,
                            courseWorkId = courseWorkId,
                        ).map { it.toStudentSubmissionListDomainModel() }
                emit(Result.Success(studentSubmissions))
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
