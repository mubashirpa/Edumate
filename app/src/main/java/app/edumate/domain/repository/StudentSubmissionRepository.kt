package app.edumate.domain.repository

import app.edumate.data.remote.dto.comment.CommentDto
import app.edumate.data.remote.dto.studentSubmission.AssignmentSubmissionDto
import app.edumate.data.remote.dto.studentSubmission.StudentSubmissionDto

interface StudentSubmissionRepository {
    suspend fun getStudentSubmissions(
        courseId: String,
        courseWorkId: String,
    ): List<StudentSubmissionDto>

    suspend fun getStudentSubmission(
        courseId: String,
        courseWorkId: String,
        userId: String,
    ): StudentSubmissionDto

    suspend fun modifyStudentSubmissionAttachments(
        id: String,
        attachments: AssignmentSubmissionDto?,
    ): StudentSubmissionDto

    suspend fun updateStudentSubmission(
        id: String,
        updates: StudentSubmissionDto,
    ): StudentSubmissionDto

    suspend fun reclaimStudentSubmission(id: String)

    suspend fun returnStudentSubmission(id: String)

    suspend fun turnInStudentSubmission(
        courseWorkId: String,
        id: String,
    )

    suspend fun createComment(
        courseId: String,
        submissionId: String,
        userId: String,
        text: String,
    ): CommentDto

    suspend fun getComments(submissionId: String): List<CommentDto>
}
