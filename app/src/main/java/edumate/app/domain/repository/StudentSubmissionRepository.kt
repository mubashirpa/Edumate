package edumate.app.domain.repository

import edumate.app.data.remote.dto.StudentSubmissionDto

interface StudentSubmissionRepository {

    /**
     * Returns a student submission.
     * @param courseId Identifier of the course.
     * @param courseWorkId Identifier of the course work.
     * @param id Identifier of the student submission.
     */
    suspend fun get(courseId: String, courseWorkId: String, id: String): StudentSubmissionDto?

    /**
     * Returns a list of student submissions that the requester is permitted to view.
     * @param courseId Identifier of the course.
     * @param courseWorkId Identifier of the student work to request.
     */
    suspend fun list(courseId: String, courseWorkId: String): List<StudentSubmissionDto>

    /**
     * Turns in a student submission.
     * @param courseId Identifier of the course.
     * @param courseWorkId Identifier of the course work.
     * @param id Identifier of the student submission.
     */
    suspend fun turnIn(
        courseId: String,
        courseWorkId: String,
        id: String,
        studentSubmission: StudentSubmissionDto
    )
}