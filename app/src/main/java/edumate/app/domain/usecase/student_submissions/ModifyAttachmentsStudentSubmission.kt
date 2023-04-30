package edumate.app.domain.usecase.student_submissions

import edumate.app.R.string as Strings
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.data.remote.mapper.toStudentSubmission
import edumate.app.domain.model.student_submissions.Attachment
import edumate.app.domain.model.student_submissions.StudentSubmission
import edumate.app.domain.repository.StudentSubmissionRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ModifyAttachmentsStudentSubmission @Inject constructor(
    private val studentSubmissionRepository: StudentSubmissionRepository
) {
    operator fun invoke(
        courseId: String,
        courseWorkId: String,
        id: String,
        addAttachments: List<Attachment>
    ): Flow<Resource<StudentSubmission?>> = flow {
        try {
            emit(Resource.Loading())
            val studentSubmission = studentSubmissionRepository.modifyAttachments(
                courseId,
                courseWorkId,
                id,
                addAttachments
            )?.toStudentSubmission()
            emit(Resource.Success(studentSubmission))
        } catch (e: Exception) {
            emit(Resource.Error(UiText.StringResource(Strings.unable_to_add_attachments)))
        }
    }
}