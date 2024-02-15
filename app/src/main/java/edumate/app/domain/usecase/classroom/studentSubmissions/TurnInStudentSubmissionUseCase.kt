package edumate.app.domain.usecase.classroom.studentSubmissions

import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.domain.repository.StudentSubmissionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import edumate.app.R.string as Strings

class TurnInStudentSubmissionUseCase
    @Inject
    constructor(
        private val studentSubmissionRepository: StudentSubmissionRepository,
    ) {
        operator fun invoke(
            courseId: String,
            courseWorkId: String,
            id: String,
        ): Flow<Result<String>> =
            flow {
                try {
                    emit(Result.Loading())
                    studentSubmissionRepository.turnIn(courseId, courseWorkId, id)
                    emit(Result.Success(id))
                } catch (e: Exception) {
                    emit(Result.Error(UiText.StringResource(Strings.unable_to_submit_student_submission)))
                }
            }
    }
