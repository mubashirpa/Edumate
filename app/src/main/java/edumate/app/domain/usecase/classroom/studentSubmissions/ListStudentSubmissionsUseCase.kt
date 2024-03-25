package edumate.app.domain.usecase.classroom.studentSubmissions

import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.data.mapper.toStudentSubmissionDomainModel
import edumate.app.data.remote.dto.classroom.studentSubmissions.SubmissionState
import edumate.app.domain.model.classroom.studentSubmissions.StudentSubmission
import edumate.app.domain.repository.AuthenticationRepository
import edumate.app.domain.repository.LateValues
import edumate.app.domain.repository.StudentSubmissionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import edumate.app.R.string as Strings

class ListStudentSubmissionsUseCase
    @Inject
    constructor(
        private val authenticationRepository: AuthenticationRepository,
        private val studentSubmissionRepository: StudentSubmissionRepository,
    ) {
        operator fun invoke(
            courseId: String,
            courseWorkId: String,
            late: LateValues? = null,
            pageSize: Int? = null,
            page: Int? = null,
            states: List<SubmissionState>? = null,
            userId: String? = null,
        ): Flow<Result<List<StudentSubmission>>> =
            flow {
                try {
                    emit(Result.Loading())
                    val idToken = authenticationRepository.getIdToken()
                    val studentSubmissions =
                        studentSubmissionRepository.list(
                            idToken,
                            courseId,
                            courseWorkId,
                            late,
                            pageSize,
                            page,
                            states,
                            userId,
                        ).studentSubmissions?.map { it.toStudentSubmissionDomainModel() }
                    emit(Result.Success(studentSubmissions))
                } catch (e: Exception) {
                    emit(
                        Result.Error(
                            UiText.StringResource(Strings.cannot_retrieve_student_submissions_at_this_time_please_try_again_later),
                        ),
                    )
                }
            }
    }
