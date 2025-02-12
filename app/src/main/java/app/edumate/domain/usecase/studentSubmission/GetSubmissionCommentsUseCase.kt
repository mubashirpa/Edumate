package app.edumate.domain.usecase.studentSubmission

import app.edumate.core.Result
import app.edumate.core.utils.execute
import app.edumate.data.mapper.toComment
import app.edumate.domain.model.comment.Comment
import app.edumate.domain.repository.StudentSubmissionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class GetSubmissionCommentsUseCase(
    private val studentSubmissionRepository: StudentSubmissionRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    operator fun invoke(submissionId: String): Flow<Result<List<Comment>>> =
        execute(ioDispatcher) {
            studentSubmissionRepository.getComments(submissionId).map { it.toComment() }
        }
}
