package app.edumate.domain.repository

import app.edumate.data.remote.dto.studentSubmission.AssignmentSubmissionDto
import app.edumate.data.remote.dto.studentSubmission.StudentSubmissionDto
import app.edumate.data.remote.dto.studentSubmission.StudentSubmissionListDto

interface StudentSubmissionRepository {
    suspend fun getStudentSubmissions(
        courseId: String,
        courseWorkId: String,
    ): List<StudentSubmissionListDto>

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
