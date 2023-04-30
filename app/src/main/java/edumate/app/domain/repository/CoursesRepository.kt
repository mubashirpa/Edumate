package edumate.app.domain.repository

import edumate.app.data.remote.dto.CourseDto
import edumate.app.domain.model.courses.CourseState

interface CoursesRepository {

    /**
     * Creates a course.
     * @param courseDto Instance of [CourseDto].
     * @return If successful, the response body contains a newly created instance of [CourseDto].
     */
    suspend fun create(courseDto: CourseDto): CourseDto?

    /**
     * Deletes a course.
     * @param id Identifier of the course to delete.
     */
    suspend fun delete(id: String)

    /**
     * Returns a course.
     * @param id Identifier of the course to return.
     * @return If successful, the response body contains an instance of [CourseDto].
     */
    suspend fun get(id: String): CourseDto?

    /**
     * Returns a list of courses that the requesting userProfile is permitted to view.
     * @param studentId Restricts returned courses to those having a student with the specified identifier.
     * @param teacherId Restricts returned courses to those having a teacher with the specified identifier.
     * @param courseState Restricts returned courses to the specified state.
     * @param pageSize Maximum number of items to return.
     * @return @return If successful, the response body contains a list of [CourseDto]
     */
    suspend fun list(
        studentId: String? = null,
        teacherId: String? = null,
        courseState: CourseState = CourseState.ACTIVE,
        pageSize: Int? = null
    ): List<CourseDto>

    /**
     * Updates a course.
     * @param id Identifier of the course to update.
     * @param courseDto Instance of [CourseDto].
     */
    suspend fun update(id: String, courseDto: CourseDto)
}