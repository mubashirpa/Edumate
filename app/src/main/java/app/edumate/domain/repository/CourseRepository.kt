package app.edumate.domain.repository

import app.edumate.data.remote.dto.course.CourseDto
import app.edumate.data.remote.dto.course.CourseWithMembersDto
import app.edumate.data.remote.dto.course.CoursesDto

interface CourseRepository {
    suspend fun createCourse(course: CourseDto): CourseDto

    suspend fun getCourses(userId: String): List<CoursesDto>

    suspend fun getCourse(id: String): CourseDto?

    suspend fun getCourseWithCurrentUser(
        id: String,
        userId: String,
    ): CourseWithMembersDto?

    suspend fun updateCourse(
        id: String,
        name: String?,
        room: String?,
        section: String?,
        subject: String?,
    ): CourseDto

    suspend fun deleteCourse(id: String): CourseDto
}
