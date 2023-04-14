package edumate.app.domain.usecase.student_submission

import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.data.remote.mapper.toStudentSubmissionDto
import edumate.app.domain.model.student_submission.StudentSubmission
import edumate.app.domain.repository.StudentSubmissionRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TurnInStudentSubmissionUseCase @Inject constructor(
    private val studentSubmissionRepository: StudentSubmissionRepository
) {
    operator fun invoke(
        courseId: String,
        courseWorkId: String,
        id: String,
        studentSubmission: StudentSubmission
    ): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())
            studentSubmissionRepository.turnIn(
                courseId,
                courseWorkId,
                id,
                studentSubmission.toStudentSubmissionDto()
            )
            emit(Resource.Success(id))
        } catch (e: Exception) {
            emit(Resource.Error(UiText.DynamicString("${e.message}")))
        }
    }
}