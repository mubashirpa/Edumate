package edumate.app.domain.repository

import edumate.app.data.remote.dto.classroom.courseWork.CourseWork
import edumate.app.data.remote.dto.classroom.courseWork.CourseWorkDto
import edumate.app.data.remote.dto.classroom.courseWork.CourseWorkState

interface CourseWorkRepository {
    /**
     * Creates course work.
     * @param accessToken JWT (JSON Web Token) that contains claims about the user.
     * @param courseId Identifier of the course.
     * @param courseWork an Instance of [CourseWork].
     * @return If successful, the response body contains a newly created instance of [CourseWork].
     */
    suspend fun create(
        accessToken: String,
        courseId: String,
        courseWork: CourseWork,
    ): CourseWork

    /**
     * Deletes a course work.
     * @param accessToken JWT (JSON Web Token) that contains claims about the user.
     * @param courseId Identifier of the course.
     * @param id Identifier of the course work to delete.
     */
    suspend fun delete(
        accessToken: String,
        courseId: String,
        id: String,
    )

    /**
     * Returns course work.
     * @param accessToken JWT (JSON Web Token) that contains claims about the user.
     * @param courseId Identifier of the course.
     * @param id Identifier of the course work.
     * @return If successful, the response body contains an instance of [CourseWork].
     */
    suspend fun get(
        accessToken: String,
        courseId: String,
        id: String,
    ): CourseWork

    /**
     * Returns a list of course work that the requester is permitted to view.
     * @param accessToken JWT (JSON Web Token) that contains claims about the user.
     * @param courseId Identifier of the course.
     * @param courseWorkStates Restriction on the work status to return. Only courseWork that
     * matches is returned. If unspecified, items with a work status of PUBLISHED is returned.
     * @param orderBy Optional sort ordering for results. Supported fields are updateTime and
     * dueDate. Supported direction keywords are asc and desc. If not specified, updateTime desc is
     * the default behavior. Examples: dueDate asc, updateTime desc, updateTime, dueDate desc.
     * @param pageSize Maximum number of items to return. Zero or unspecified indicates that the
     * server may assign a maximum.
     * @param page nextPage value returned from a previous list call, indicating that the
     * subsequent page of results should be returned.
     * @return If successful, the response body contains an instance of [CourseWorkDto].
     */
    suspend fun list(
        accessToken: String,
        courseId: String,
        courseWorkStates: List<CourseWorkState>? = listOf(CourseWorkState.PUBLISHED),
        orderBy: String? = "updateTime desc",
        pageSize: Int? = null,
        page: Int? = null,
    ): CourseWorkDto

    /**
     * Updates one or more fields of a course work.
     * @param accessToken JWT (JSON Web Token) that contains claims about the user.
     * @param courseId Identifier of the course.
     * @param id Identifier of the course work.
     * @param updateMask Mask that identifies which fields on the course work to update.
     * @param courseWork An instance of [CourseWork].
     * @return If successful, the response body contains an instance of [CourseWork].
     */
    suspend fun patch(
        accessToken: String,
        courseId: String,
        id: String,
        updateMask: String,
        courseWork: CourseWork,
    ): CourseWork
}
