package app.edumate.domain.usecase.studentSubmission

import app.edumate.core.Result
import app.edumate.core.utils.execute
import app.edumate.domain.repository.StudentSubmissionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class ReturnStudentSubmissionUseCase(
    private val studentSubmissionRepository: StudentSubmissionRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    operator fun invoke(id: String): Flow<Result<Boolean>> =
        execute(ioDispatcher) {
            studentSubmissionRepository.returnStudentSubmission(id)
            true
        }
}
