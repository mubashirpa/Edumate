package app.edumate.domain.usecase.studentSubmission

import app.edumate.core.Result
import app.edumate.core.UnauthenticatedAccessException
import app.edumate.core.utils.execute
import app.edumate.data.mapper.toComment
import app.edumate.domain.model.comment.Comment
import app.edumate.domain.repository.AuthenticationRepository
import app.edumate.domain.repository.StudentSubmissionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class CreateSubmissionCommentUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val studentSubmissionRepository: StudentSubmissionRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    operator fun invoke(
        courseId: String,
        submissionId: String,
        text: String,
    ): Flow<Result<Comment>> =
        execute(ioDispatcher) {
            val userId =
                authenticationRepository.currentUser()?.id ?: throw UnauthenticatedAccessException()
            studentSubmissionRepository
                .createComment(courseId, submissionId, userId, text)
                .toComment()
        }
}
