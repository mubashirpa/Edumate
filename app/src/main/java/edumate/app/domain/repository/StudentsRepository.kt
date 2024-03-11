package edumate.app.domain.repository

import edumate.app.data.remote.dto.classroom.students.Student
import edumate.app.data.remote.dto.classroom.students.StudentsDto

interface StudentsRepository {
    /**
     * Adds a user as a student of a course.
     * @param accessToken JWT (JSON Web Token) that contains claims about the user.
     * @param courseId Identifier of the course to create the student in.
     * @param enrollmentCode Enrollment code of the course to create the student in.
     * @param student An instance of Student.
     * @return If successful, the response body contains a newly created instance of [Student].
     */
    suspend fun create(
        accessToken: String,
        courseId: String,
        enrollmentCode: String? = null,
        student: Student,
    ): Student

    /**
     * Deletes a student of a course.
     * @param accessToken JWT (JSON Web Token) that contains claims about the user.
     * @param courseId Identifier of the course.
     * @param userId Identifier of the student to delete.
     */
    suspend fun delete(
        accessToken: String,
        courseId: String,
        userId: String,
    )

    /**
     * Returns a student of a course.
     * @param accessToken JWT (JSON Web Token) that contains claims about the user.
     * @param courseId Identifier of the course.
     * @param userId Identifier of the student to return.
     * @return If successful, the response body contains an instance of [Student].
     */
    suspend fun get(
        accessToken: String,
        courseId: String,
        userId: String,
    ): Student

    /**
     * Returns a list of students of this course that the requester is permitted to view.
     * @param accessToken JWT (JSON Web Token) that contains claims about the user.
     * @param courseId Identifier of the course.
     * @param pageSize Maximum number of items to return. The default is 30 if unspecified or 0.
     * @param page nextPage value returned from a previous list call, indicating that the subsequent
     * page of results should be returned.
     * @return If successful, the response body contains an instance of [StudentsDto].
     */
    suspend fun list(
        accessToken: String,
        courseId: String,
        pageSize: Int? = 30,
        page: Int? = null,
    ): StudentsDto
}
