package edumate.app.domain.repository

import edumate.app.data.remote.dto.classroom.courses.Course
import edumate.app.data.remote.dto.classroom.courses.CoursesDto
import edumate.app.domain.model.classroom.courses.CourseState

interface CoursesRepository {
    /**
     * Creates a course.
     * @param accessToken JWT (JSON Web Token) that contains claims about the user.
     * @param course Instance of [Course].
     * @return If successful, the response body contains a newly created instance of [Course].
     */
    suspend fun create(
        accessToken: String,
        course: Course,
    ): Course?

    /**
     * Deletes a course.
     * @param accessToken JWT (JSON Web Token) that contains claims about the user.
     * @param id Identifier of the course to delete.
     */
    suspend fun delete(
        accessToken: String,
        id: String,
    )

    /**
     * Returns a course.
     * @param accessToken JWT (JSON Web Token) that contains claims about the user.
     * @param id Identifier of the course to return.
     * @return If successful, the response body contains an instance of [Course].
     */
    suspend fun get(
        accessToken: String,
        id: String,
    ): Course?

    /**
     * Returns a list of courses that the requesting user is permitted to view, restricted to those
     * that match the request. Returned courses are ordered by creation time, with the most recently
     * created coming first.
     * @param accessToken JWT (JSON Web Token) that contains claims about the user.
     * @param courseStates Restricts returned courses to those in one of the specified states. The
     * default value is ACTIVE, ARCHIVED, PROVISIONED, DECLINED.
     * @param pageSize Maximum number of items to return.
     * @param page nextPage value returned from a previous list call, indicating that the
     * subsequent page of results should be returned.
     * @param studentId Restricts returned courses to those having a student with the specified
     * identifier.
     * @param teacherId Restricts returned courses to those having a teacher with the specified
     * identifier.
     * @return @return If successful, the response body contains an instance of [CoursesDto].
     */
    suspend fun list(
        accessToken: String,
        courseStates: List<CourseState>? =
            listOf(
                CourseState.ACTIVE,
                CourseState.ARCHIVED,
                CourseState.PROVISIONED,
                CourseState.DECLINED,
            ),
        pageSize: Int? = null,
        page: Int? = null,
        studentId: String? = null,
        teacherId: String? = null,
    ): CoursesDto

    /**
     * Updates a course.
     * @param accessToken JWT (JSON Web Token) that contains claims about the user.
     * @param id Identifier of the course to update.
     * @param course Instance of [Course].
     */
    suspend fun update(
        accessToken: String,
        id: String,
        course: Course,
    )
}
