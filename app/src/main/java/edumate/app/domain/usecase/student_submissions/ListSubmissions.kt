package edumate.app.domain.usecase.student_submissions

import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.data.remote.mapper.toStudentSubmission
import edumate.app.domain.model.student_submissions.StudentSubmission
import edumate.app.domain.repository.StudentSubmissionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import edumate.app.R.string as Strings

class ListSubmissions
    @Inject
    constructor(
        private val studentSubmissionRepository: StudentSubmissionRepository,
    ) {
        operator fun invoke(
            courseId: String,
            courseWorkId: String,
        ): Flow<Resource<List<StudentSubmission>>> =
            flow {
                try {
                    emit(Resource.Loading())
                    val studentSubmissions =
                        studentSubmissionRepository.list(courseId, courseWorkId)
                            .map { it.toStudentSubmission() }
                    emit(Resource.Success(studentSubmissions))
                } catch (e: Exception) {
                    emit(
                        Resource.Error(
                            UiText.StringResource(
                                Strings.cannot_retrieve_student_submissions_at_this_time_please_try_again_later,
                            ),
                        ),
                    )
                }
            }
    }
