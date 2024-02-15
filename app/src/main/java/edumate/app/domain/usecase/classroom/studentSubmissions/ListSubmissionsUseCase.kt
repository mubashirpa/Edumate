package edumate.app.domain.usecase.classroom.studentSubmissions

import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.data.mapper.toStudentSubmissionDomainModel
import edumate.app.data.remote.dto.classroom.studentSubmissions.SubmissionState
import edumate.app.domain.model.classroom.studentSubmissions.StudentSubmission
import edumate.app.domain.repository.LateValues
import edumate.app.domain.repository.StudentSubmissionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import edumate.app.R.string as Strings

class ListSubmissionsUseCase
    @Inject
    constructor(
        private val studentSubmissionRepository: StudentSubmissionRepository,
    ) {
        operator fun invoke(
            courseId: String,
            courseWorkId: String,
            userId: String? = null,
            states: List<SubmissionState>? = null,
            late: LateValues? = null,
            pageSize: Int? = null,
            pageToken: String? = null,
        ): Flow<Result<List<StudentSubmission>>> =
            flow {
                try {
                    emit(Result.Loading())
                    val studentSubmissions =
                        studentSubmissionRepository.list(
                            courseId,
                            courseWorkId,
                            userId,
                            states,
                            late,
                            pageSize,
                            pageToken,
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
