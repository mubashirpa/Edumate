package edumate.app.domain.repository

import edumate.app.data.remote.dto.UserProfileDto

interface TeachersRepository {

    /**
     * Creates a teacher of a course.
     * @param courseId Identifier of the course to create the student in.
     * @param userId Identifier of the teacher to create.
     */
    suspend fun create(courseId: String, userId: String)

    /**
     * Removes the specified teacher from the specified course.
     * @param courseId Identifier of the course.
     * @param userId Identifier of the teacher to delete.
     */
    suspend fun delete(courseId: String, userId: String)

    /**
     * Returns a teacher of a course.
     * @param courseId Identifier of the course.
     * @param userId Identifier of the teacher to return.
     * @return @return If successful, the response body contains an instance of [UserProfileDto].
     */
    suspend fun get(courseId: String, userId: String): UserProfileDto

    /**
     * Returns a list of students of this course that the requester is permitted to view.
     * @param courseId Identifier of the course.
     * @param pageSize Maximum number of items to return. The default is 30 if unspecified or 0.
     * @return If successful, the response body contains a list of [UserProfileDto]
     */
    suspend fun list(courseId: String, pageSize: Int? = 30): List<UserProfileDto>
}