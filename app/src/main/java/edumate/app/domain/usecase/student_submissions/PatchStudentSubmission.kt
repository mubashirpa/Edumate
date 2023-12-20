package edumate.app.domain.usecase.student_submissions

import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.data.remote.mapper.toStudentSubmission
import edumate.app.data.remote.mapper.toStudentSubmissionDto
import edumate.app.domain.model.student_submissions.StudentSubmission
import edumate.app.domain.repository.StudentSubmissionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import edumate.app.R.string as Strings

class PatchStudentSubmission
    @Inject
    constructor(
        private val studentSubmissionRepository: StudentSubmissionRepository,
    ) {
        operator fun invoke(
            courseId: String,
            courseWorkId: String,
            id: String,
            studentSubmission: StudentSubmission,
        ): Flow<Resource<StudentSubmission?>> =
            flow {
                try {
                    emit(Resource.Loading())
                    val submission =
                        studentSubmissionRepository.patch(
                            courseId,
                            courseWorkId,
                            id,
                            studentSubmission.toStudentSubmissionDto(),
                        )?.toStudentSubmission()
                    emit(Resource.Success(submission))
                } catch (e: Exception) {
                    emit(Resource.Error(UiText.StringResource(Strings.unable_to_update_student_submission)))
                }
            }
    }
