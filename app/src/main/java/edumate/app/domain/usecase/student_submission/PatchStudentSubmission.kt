package edumate.app.domain.usecase.student_submission

import edumate.app.R.string as Strings
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.data.remote.mapper.toStudentSubmission
import edumate.app.data.remote.mapper.toStudentSubmissionDto
import edumate.app.domain.model.student_submission.StudentSubmission
import edumate.app.domain.repository.StudentSubmissionRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PatchStudentSubmission @Inject constructor(
    private val studentSubmissionRepository: StudentSubmissionRepository
) {
    operator fun invoke(
        courseId: String,
        courseWorkId: String,
        id: String,
        studentSubmission: StudentSubmission
    ): Flow<Resource<StudentSubmission?>> = flow {
        try {
            emit(Resource.Loading())
            val submission = studentSubmissionRepository.patch(
                courseId,
                courseWorkId,
                id,
                studentSubmission.toStudentSubmissionDto()
            )?.toStudentSubmission()
            emit(Resource.Success(submission))
        } catch (e: Exception) {
            emit(Resource.Error(UiText.StringResource(Strings.error_unexpected)))
        }
    }
}