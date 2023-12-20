package edumate.app.domain.usecase.student_submissions

import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.domain.repository.StudentSubmissionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import edumate.app.R.string as Strings

class TurnInStudentSubmission
    @Inject
    constructor(
        private val studentSubmissionRepository: StudentSubmissionRepository,
    ) {
        operator fun invoke(
            courseId: String,
            courseWorkId: String,
            id: String,
        ): Flow<Resource<String>> =
            flow {
                try {
                    emit(Resource.Loading())
                    studentSubmissionRepository.turnIn(courseId, courseWorkId, id)
                    emit(Resource.Success(id))
                } catch (e: Exception) {
                    emit(Resource.Error(UiText.StringResource(Strings.unable_to_submit_student_submission)))
                }
            }
    }
