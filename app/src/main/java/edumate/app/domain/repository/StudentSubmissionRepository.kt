package edumate.app.domain.repository

import edumate.app.data.remote.dto.classroom.studentSubmissions.ModifyAttachments
import edumate.app.data.remote.dto.classroom.studentSubmissions.StudentSubmission
import edumate.app.data.remote.dto.classroom.studentSubmissions.StudentSubmissionsDto
import edumate.app.data.remote.dto.classroom.studentSubmissions.SubmissionState

interface StudentSubmissionRepository {
    /**
     * Returns a student submission.
     * @param accessToken JWT (JSON Web Token) that contains claims about the user.
     * @param courseId Identifier of the course.
     * @param courseWorkId Identifier of the course work.
     * @param id Identifier of the student submission.
     * @return If successful, the response body contains an instance of [StudentSubmission].
     */
    suspend fun get(
        accessToken: String,
        courseId: String,
        courseWorkId: String,
        id: String,
    ): StudentSubmission

    /**
     * Returns a list of student submissions that the requester is permitted to view.
     * @param accessToken JWT (JSON Web Token) that contains claims about the user.
     * @param courseId Identifier of the course.
     * @param courseWorkId Identifier of the student work to request.
     * @param late Requested lateness value. If specified, returned student submissions are
     * restricted by the requested value. If unspecified, submissions are returned regardless of
     * late value.
     * @param pageSize Maximum number of items to return. Zero or unspecified indicates that the
     * server may assign a maximum.
     * @param page nextPage value returned from a previous list call, indicating that the subsequent
     * page of results should be returned.
     * @param states Requested submission states. If specified, returned student submissions match
     * one of the specified submission states.
     * @param userId Optional argument to restrict returned student work to those owned by the
     * student with the specified identifier.
     * @return If successful, the response body contains an instance of [StudentSubmissionsDto].
     */
    suspend fun list(
        accessToken: String,
        courseId: String,
        courseWorkId: String,
        late: LateValues? = null,
        pageSize: Int? = null,
        page: Int? = null,
        states: List<SubmissionState>? = null,
        userId: String? = null,
    ): StudentSubmissionsDto

    /**
     * Modifies attachments of student submission.
     * @param accessToken JWT (JSON Web Token) that contains claims about the user.
     * @param courseId Identifier of the course.
     * @param courseWorkId Identifier of the course work.
     * @param id Identifier of the student submission.
     * @param modifyAttachments An instance of [ModifyAttachments]
     * @return If successful, the response body contains an instance of [StudentSubmission].
     */
    suspend fun modifyAttachments(
        accessToken: String,
        courseId: String,
        courseWorkId: String,
        id: String,
        modifyAttachments: ModifyAttachments,
    ): StudentSubmission

    /**
     * Updates one or more fields of a student submission.
     * @param accessToken JWT (JSON Web Token) that contains claims about the user.
     * @param courseId Identifier of the course.
     * @param courseWorkId Identifier of the course work.
     * @param id Identifier of the student submission.
     * @param updateMask Mask that identifies which fields on the student submission to update.
     * @param studentSubmission An instance of [StudentSubmission].
     * @return If successful, the response body contains an instance of [StudentSubmission].
     */
    suspend fun patch(
        accessToken: String,
        courseId: String,
        courseWorkId: String,
        id: String,
        updateMask: String,
        studentSubmission: StudentSubmission,
    ): StudentSubmission

    /**
     * Reclaims a student submission on behalf of the student that owns it.
     * @param accessToken JWT (JSON Web Token) that contains claims about the user.
     * @param courseId Identifier of the course.
     * @param courseWorkId Identifier of the course work.
     * @param id Identifier of the student submission.
     */
    suspend fun reclaim(
        accessToken: String,
        courseId: String,
        courseWorkId: String,
        id: String,
    )

    /**
     * Returns a student submission.
     * @param accessToken JWT (JSON Web Token) that contains claims about the user.
     * @param courseId Identifier of the course.
     * @param courseWorkId Identifier of the course work.
     * @param id Identifier of the student submission.
     */
    suspend fun `return`(
        accessToken: String,
        courseId: String,
        courseWorkId: String,
        id: String,
    )

    /**
     * Turns in a student submission.
     * @param accessToken JWT (JSON Web Token) that contains claims about the user.
     * @param courseId Identifier of the course.
     * @param courseWorkId Identifier of the course work.
     * @param id Identifier of the student submission.
     */
    suspend fun turnIn(
        accessToken: String,
        courseId: String,
        courseWorkId: String,
        id: String,
    )
}

enum class LateValues {
    LATE_ONLY,
    LATE_VALUES_UNSPECIFIED,
    NOT_LATE_ONLY,
}
