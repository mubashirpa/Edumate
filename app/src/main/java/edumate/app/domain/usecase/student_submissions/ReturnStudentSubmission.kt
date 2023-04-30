package edumate.app.domain.usecase.student_submissions

import edumate.app.R.string as Strings
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.domain.repository.StudentSubmissionRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ReturnStudentSubmission @Inject constructor(
    private val studentSubmissionRepository: StudentSubmissionRepository
) {
    operator fun invoke(
        courseId: String,
        courseWorkId: String,
        id: String
    ): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())
            studentSubmissionRepository.`return`(courseId, courseWorkId, id)
            emit(Resource.Success(id))
        } catch (e: Exception) {
            emit(Resource.Error(UiText.StringResource(Strings.unable_to_return_student_submission)))
        }
    }
}