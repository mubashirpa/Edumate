package app.edumate.domain.usecase.studentSubmission

import app.edumate.core.Result
import app.edumate.core.utils.execute
import app.edumate.data.mapper.toAssignmentSubmissionDto
import app.edumate.data.mapper.toStudentSubmissionDomainModel
import app.edumate.domain.model.material.Material
import app.edumate.domain.model.studentSubmission.AssignmentSubmission
import app.edumate.domain.model.studentSubmission.StudentSubmission
import app.edumate.domain.repository.StudentSubmissionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class ModifyStudentSubmissionAttachmentsUseCase(
    private val studentSubmissionRepository: StudentSubmissionRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    operator fun invoke(
        id: String,
        attachments: List<Material>?,
    ): Flow<Result<StudentSubmission>> =
        execute(ioDispatcher) {
            val assignmentSubmission =
                if (attachments.isNullOrEmpty()) {
                    null
                } else {
                    AssignmentSubmission(attachments)
                }?.toAssignmentSubmissionDto()
            studentSubmissionRepository
                .modifyStudentSubmissionAttachments(
                    id = id,
                    attachments = assignmentSubmission,
                ).toStudentSubmissionDomainModel()
        }
}
