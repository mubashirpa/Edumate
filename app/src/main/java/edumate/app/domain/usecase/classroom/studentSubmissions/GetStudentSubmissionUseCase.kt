package edumate.app.domain.usecase.classroom.studentSubmissions

import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.data.mapper.toStudentSubmissionDomainModel
import edumate.app.domain.model.classroom.studentSubmissions.StudentSubmission
import edumate.app.domain.repository.StudentSubmissionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import edumate.app.R.string as Strings

class GetStudentSubmissionUseCase
    @Inject
    constructor(
        private val studentSubmissionRepository: StudentSubmissionRepository,
    ) {
        operator fun invoke(
            courseId: String,
            courseWorkId: String,
            id: String,
        ): Flow<Result<StudentSubmission>> =
            flow {
                try {
                    emit(Result.Loading())
                    val studentSubmission =
                        studentSubmissionRepository.get(courseId, courseWorkId, id)
                            .toStudentSubmissionDomainModel()
                    emit(Result.Success(studentSubmission))
                } catch (e: Exception) {
                    emit(
                        Result.Error(UiText.StringResource(Strings.cannot_retrieve_student_submission_at_this_time_please_try_again_later)),
                    )
                }
            }
    }
