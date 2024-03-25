package edumate.app.domain.usecase.classroom.studentSubmissions

import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.data.mapper.toStudentSubmission
import edumate.app.data.mapper.toStudentSubmissionDomainModel
import edumate.app.domain.model.classroom.studentSubmissions.StudentSubmission
import edumate.app.domain.repository.AuthenticationRepository
import edumate.app.domain.repository.StudentSubmissionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import edumate.app.R.string as Strings

class PatchStudentSubmissionUseCase
    @Inject
    constructor(
        private val authenticationRepository: AuthenticationRepository,
        private val studentSubmissionRepository: StudentSubmissionRepository,
    ) {
        operator fun invoke(
            courseId: String,
            courseWorkId: String,
            id: String,
            updateMask: String,
            studentSubmission: StudentSubmission,
        ): Flow<Result<StudentSubmission>> =
            flow {
                try {
                    emit(Result.Loading())
                    val idToken = authenticationRepository.getIdToken()
                    val studentSubmissionResponse =
                        studentSubmissionRepository.patch(
                            idToken,
                            courseId,
                            courseWorkId,
                            id,
                            updateMask,
                            studentSubmission.toStudentSubmission(),
                        ).toStudentSubmissionDomainModel()
                    emit(Result.Success(studentSubmissionResponse))
                } catch (e: Exception) {
                    emit(Result.Error(UiText.StringResource(Strings.unable_to_update_student_submission)))
                }
            }
    }
