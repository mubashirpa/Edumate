package app.edumate.domain.usecase.studentSubmission

import app.edumate.core.Result
import app.edumate.core.utils.execute
import app.edumate.data.mapper.toStudentSubmissionDomainModel
import app.edumate.domain.model.studentSubmission.StudentSubmission
import app.edumate.domain.repository.StudentSubmissionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class GetStudentSubmissionsUseCase(
    private val studentSubmissionRepository: StudentSubmissionRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    operator fun invoke(
        courseId: String,
        courseWorkId: String,
    ): Flow<Result<List<StudentSubmission>>> =
        execute(ioDispatcher) {
            studentSubmissionRepository
                .getStudentSubmissions(
                    courseId = courseId,
                    courseWorkId = courseWorkId,
                ).map { it.toStudentSubmissionDomainModel() }
        }
}
