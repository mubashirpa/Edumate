package edumate.app.domain.repository

import edumate.app.data.remote.dto.CourseWorkDto

interface CourseWorkRepository {

    /**
     * Creates course work.
     * @param courseId Identifier of the course.
     */
    suspend fun create(courseId: String, courseWorkDto: CourseWorkDto): CourseWorkDto?

    /**
     * Deletes a course work.
     * @param courseId Identifier of the course.
     * @param id Identifier of the course work to delete.
     */
    suspend fun delete(courseId: String, id: String)

    /**
     * Returns course work.
     * @param courseId Identifier of the course.
     * @param id Identifier of the course work.
     */
    suspend fun get(courseId: String, id: String): CourseWorkDto?

    /**
     * Returns a list of course work that the requester is permitted to view.
     * @param courseId Identifier of the course.
     */
    suspend fun list(courseId: String): List<CourseWorkDto>

    /**
     * Updates one or more fields of a course work.
     * @param courseId Identifier of the course.
     * @param id Identifier of the course work.
     */
    suspend fun patch(courseId: String, id: String, courseWorkDto: CourseWorkDto): CourseWorkDto?
}