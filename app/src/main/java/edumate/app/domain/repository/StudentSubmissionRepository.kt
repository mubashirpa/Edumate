package edumate.app.domain.repository

import edumate.app.data.remote.dto.classroom.studentSubmissions.StudentSubmission
import edumate.app.data.remote.dto.classroom.studentSubmissions.StudentSubmissionsDto
import edumate.app.data.remote.dto.classroom.studentSubmissions.SubmissionState

interface StudentSubmissionRepository {
    /**
     * Returns a student submission.
     * @param courseId Identifier of the course.
     * @param courseWorkId Identifier of the course work.
     * @param id Identifier of the student submission.
     * @return If successful, the response body contains an instance of [StudentSubmission].
     */
    suspend fun get(
        courseId: String,
        courseWorkId: String,
        id: String,
    ): StudentSubmission

    /**
     * Returns a list of student submissions that the requester is permitted to view.
     * @param courseId Identifier of the course.
     * @param courseWorkId Identifier of the student work to request.
     * @param userId Optional argument to restrict returned student work to those owned by the
     * student with the specified identifier.
     * @param states Requested submission states. If specified, returned student submissions match
     * one of the specified submission states.
     * @param late Requested lateness value. If specified, returned student submissions are
     * restricted by the requested value. If unspecified, submissions are returned regardless of
     * late value.
     * @param pageSize Maximum number of items to return. Zero or unspecified indicates that the
     * server may assign a maximum.
     * @param pageToken nextPageToken value returned from a previous list call, indicating that the
     * subsequent page of results should be returned.
     * @return If successful, the response body contains a list of [StudentSubmissionsDto].
     */
    suspend fun list(
        courseId: String,
        courseWorkId: String,
        userId: String? = null,
        states: List<SubmissionState>? = null,
        late: LateValues? = null,
        pageSize: Int? = null,
        pageToken: String? = null,
    ): StudentSubmissionsDto

    /**
     * Updates one or more fields of a student submission.
     * @param courseId Identifier of the course.
     * @param courseWorkId Identifier of the course work.
     * @param id Identifier of the student submission.
     * @param studentSubmission An instance of student submission.
     * @return If successful, the response body contains an instance of [StudentSubmission].
     */
    suspend fun update(
        courseId: String,
        courseWorkId: String,
        id: String,
        studentSubmission: StudentSubmission,
    ): StudentSubmission

    /**
     * Reclaims a student submission on behalf of the student that owns it.
     * @param courseId Identifier of the course.
     * @param courseWorkId Identifier of the course work.
     * @param id Identifier of the student submission.
     */
    suspend fun reclaim(
        courseId: String,
        courseWorkId: String,
        id: String,
    )

    /**
     * Returns a student submission.
     * @param courseId Identifier of the course.
     * @param courseWorkId Identifier of the course work.
     * @param id Identifier of the student submission.
     */
    suspend fun `return`(
        courseId: String,
        courseWorkId: String,
        id: String,
    )

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
    )
}

enum class LateValues {
    LATE_ONLY,
    LATE_VALUES_UNSPECIFIED,
    NOT_LATE_ONLY,
}
