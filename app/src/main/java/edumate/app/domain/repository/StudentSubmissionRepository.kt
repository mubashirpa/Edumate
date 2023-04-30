package edumate.app.domain.repository

import edumate.app.data.remote.dto.StudentSubmissionDto
import edumate.app.domain.model.student_submissions.Attachment

interface StudentSubmissionRepository {

    /**
     * Returns a student submission.
     * @param courseId Identifier of the course.
     * @param courseWorkId Identifier of the course work.
     * @param id Identifier of the student submission.
     * @return If successful, the response body contains an instance of [StudentSubmissionDto].
     */
    suspend fun get(courseId: String, courseWorkId: String, id: String): StudentSubmissionDto?

    /**
     * Returns a list of student submissions that the requester is permitted to view.
     * @param courseId Identifier of the course.
     * @param courseWorkId Identifier of the student work to request.
     * @return If successful, the response body contains a list of [StudentSubmissionDto].
     */
    suspend fun list(courseId: String, courseWorkId: String): List<StudentSubmissionDto>

    /**
     * Modifies attachments of student submission.
     * @param courseId Identifier of the course.
     * @param courseWorkId Identifier of the course work.
     * @param id Identifier of the student submission.
     * @param addAttachments Attachments to add. A student submission may not have more than 20 attachments.
     * @return If successful, the response body contains an instance of [StudentSubmissionDto].
     */
    suspend fun modifyAttachments(
        courseId: String,
        courseWorkId: String,
        id: String,
        addAttachments: List<Attachment>
    ): StudentSubmissionDto?

    /**
     * Updates one or more fields of a student submission.
     * @param courseId Identifier of the course.
     * @param courseWorkId Identifier of the course work.
     * @param id Identifier of the student submission.
     * @param studentSubmission Instance of student submission.
     * @return If successful, the response body contains an instance of [StudentSubmissionDto].
     */
    suspend fun patch(
        courseId: String,
        courseWorkId: String,
        id: String,
        studentSubmission: StudentSubmissionDto
    ): StudentSubmissionDto?

    /**
     * Reclaims a student submission on behalf of the student that owns it.
     * @param courseId Identifier of the course.
     * @param courseWorkId Identifier of the course work.
     * @param id Identifier of the student submission.
     */
    suspend fun reclaim(courseId: String, courseWorkId: String, id: String)

    /**
     * Returns a student submission.
     * @param courseId Identifier of the course.
     * @param courseWorkId Identifier of the course work.
     * @param id Identifier of the student submission.
     */
    suspend fun `return`(courseId: String, courseWorkId: String, id: String)

    /**
     * Turns in a student submission.
     * @param courseId Identifier of the course.
     * @param courseWorkId Identifier of the course work.
     * @param id Identifier of the student submission.
     */
    suspend fun turnIn(courseId: String, courseWorkId: String, id: String)
}