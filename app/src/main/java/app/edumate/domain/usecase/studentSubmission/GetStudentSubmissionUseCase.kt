package app.edumate.domain.usecase.studentSubmission

import app.edumate.core.Result
import app.edumate.core.UnauthenticatedAccessException
import app.edumate.core.utils.execute
import app.edumate.data.mapper.toStudentSubmissionDomainModel
import app.edumate.domain.model.studentSubmission.StudentSubmission
import app.edumate.domain.repository.AuthenticationRepository
import app.edumate.domain.repository.StudentSubmissionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class GetStudentSubmissionUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val studentSubmissionRepository: StudentSubmissionRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    operator fun invoke(
        courseId: String,
        courseWorkId: String,
    ): Flow<Result<StudentSubmission>> =
        execute(ioDispatcher) {
            val userId =
                authenticationRepository.currentUser()?.id ?: throw UnauthenticatedAccessException()
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
        execute(ioDispatcher) {
            studentSubmissionRepository
                .getStudentSubmission(
                    courseId = courseId,
                    courseWorkId = courseWorkId,
                    userId = userId,
                ).toStudentSubmissionDomainModel()
        }
}
