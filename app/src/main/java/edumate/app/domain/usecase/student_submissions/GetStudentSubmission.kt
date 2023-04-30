package edumate.app.domain.usecase.student_submissions

import edumate.app.R.string as Strings
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.data.remote.mapper.toStudentSubmission
import edumate.app.domain.model.student_submissions.StudentSubmission
import edumate.app.domain.repository.StudentSubmissionRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetStudentSubmission @Inject constructor(
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
            emit(
                Resource.Error(
                    UiText.StringResource(
                        Strings.cannot_retrieve_student_submission_at_this_time_please_try_again_later
                    )
                )
            )
        }
    }
}