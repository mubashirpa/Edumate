package app.edumate.domain.usecase.studentSubmission

import app.edumate.core.Result
import app.edumate.core.utils.execute
import app.edumate.data.mapper.toStudentSubmissionDomainModel
import app.edumate.data.mapper.toStudentSubmissionDto
import app.edumate.domain.model.studentSubmission.QuestionSubmission
import app.edumate.domain.model.studentSubmission.StudentSubmission
import app.edumate.domain.repository.StudentSubmissionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class UpdateStudentSubmissionUseCase(
    private val studentSubmissionRepository: StudentSubmissionRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    operator fun invoke(
        id: String,
        multipleChoiceAnswer: String? = null,
        shortAnswer: String? = null,
        assignedGrade: Int? = null,
    ): Flow<Result<StudentSubmission>> =
        execute(ioDispatcher) {
            val updates =
                StudentSubmission(
                    multipleChoiceSubmission = multipleChoiceAnswer?.let { QuestionSubmission(it) },
                    shortAnswerSubmission = shortAnswer?.let { QuestionSubmission(it) },
                    assignedGrade = assignedGrade,
                ).toStudentSubmissionDto()
            studentSubmissionRepository
                .updateStudentSubmission(
                    id = id,
                    updates = updates,
                ).toStudentSubmissionDomainModel()
        }
}
