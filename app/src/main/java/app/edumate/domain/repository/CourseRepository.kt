package app.edumate.domain.repository

import app.edumate.data.remote.dto.courses.CourseDto
import app.edumate.data.remote.dto.courses.CoursesDto
import app.edumate.domain.model.courses.Course

interface CourseRepository {
    suspend fun createCourse(course: Course): CourseDto

    suspend fun getCourses(userId: String): List<CoursesDto>?

    suspend fun getCourse(id: String): CourseDto?

    suspend fun updateCourse(
        id: String,
        name: String?,
        room: String?,
        section: String?,
        subject: String?,
    ): CourseDto?

    suspend fun deleteCourse(id: String): CourseDto?
}
