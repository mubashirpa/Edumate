package edumate.app.domain.usecase.student_submission

import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.data.remote.mapper.toStudentSubmission
import edumate.app.domain.model.student_submission.StudentSubmission
import edumate.app.domain.repository.StudentSubmissionRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetStudentSubmissionUseCase @Inject constructor(
    private val studentSubmissionRepository: StudentSubmissionRepository
) {
    operator fun invoke(
        courseId: String,
        courseWorkId: String,
        id: String
    ): Flow<Resource<StudentSubmission?>> = flow {
        try {
            emit(Resource.Loading())
            val studentSubmission =
                studentSubmissionRepository.get(courseId, courseWorkId, id)?.toStudentSubmission()
            emit(Resource.Success(studentSubmission))
        } catch (e: Exception) {
            emit(Resource.Error(UiText.DynamicString("${e.message}")))
        }
    }
}