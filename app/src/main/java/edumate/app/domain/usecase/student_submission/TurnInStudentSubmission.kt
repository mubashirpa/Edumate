package edumate.app.domain.usecase.student_submission

import edumate.app.R.string as Strings
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.domain.repository.StudentSubmissionRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TurnInStudentSubmission @Inject constructor(
    private val studentSubmissionRepository: StudentSubmissionRepository
) {
    operator fun invoke(
        courseId: String,
        courseWorkId: String,
        id: String
    ): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())
            studentSubmissionRepository.turnIn(courseId, courseWorkId, id)
            emit(Resource.Success(id))
        } catch (e: Exception) {
            emit(Resource.Error(UiText.StringResource(Strings.error_unexpected)))
        }
    }
}