package app.edumate.domain.repository

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
}
