package edumate.app.domain.usecase.student_submission

import edumate.app.R.string as Strings
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.data.remote.mapper.toStudentSubmission
import edumate.app.domain.model.student_submission.StudentSubmission
import edumate.app.domain.repository.StudentSubmissionRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ListSubmissions @Inject constructor(
    private val studentSubmissionRepository: StudentSubmissionRepository
) {
    operator fun invoke(
        courseId: String,
        courseWorkId: String
    ): Flow<Resource<List<StudentSubmission>>> = flow {
        try {
            emit(Resource.Loading())
            val studentSubmissions = studentSubmissionRepository.list(courseId, courseWorkId)
                .map { it.toStudentSubmission() }
            emit(Resource.Success(studentSubmissions))
        } catch (e: Exception) {
            emit(
                Resource.Error(
                    UiText.StringResource(
                        Strings.cannot_retrieve_student_works_at_this_time_lease_try_again_later
                    )
                )
            )
        }
    }
}