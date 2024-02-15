package edumate.app.domain.repository

import edumate.app.data.remote.dto.classroom.teachers.Teacher
import edumate.app.data.remote.dto.classroom.teachers.TeachersDto

interface TeachersRepository {
    /**
     * Creates a teacher of a course.
     * @param courseId Identifier of the course to create the student in.
     * @param teacher An instance of [Teacher].
     * @return If successful, the response body contains a newly created instance of [Teacher].
     */
    suspend fun create(
        courseId: String,
        teacher: Teacher,
    ): Teacher

    /**
     * Removes the specified teacher from the specified course.
     * @param courseId Identifier of the course.
     * @param userId Identifier of the teacher to delete.
     */
    suspend fun delete(
        courseId: String,
        userId: String,
    )

    /**
     * Returns a teacher of a course.
     * @param courseId Identifier of the course.
     * @param userId Identifier of the teacher to return.
     * @return @return If successful, the response body contains an instance of [Teacher].
     */
    suspend fun get(
        courseId: String,
        userId: String,
    ): Teacher

    /**
     * Returns a list of students of this course that the requester is permitted to view.
     * @param courseId Identifier of the course.
     * @param pageSize Maximum number of items to return. The default is 30 if unspecified or 0.
     * @param pageToken nextPageToken value returned from a previous list call, indicating that the
     * subsequent page of results should be returned.
     * @return If successful, the response body contains a list of [TeachersDto]
     */
    suspend fun list(
        courseId: String,
        pageSize: Int? = 30,
        pageToken: String? = null,
    ): TeachersDto
}
