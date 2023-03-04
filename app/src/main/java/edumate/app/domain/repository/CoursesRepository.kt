package edumate.app.domain.repository

import edumate.app.data.remote.dto.CoursesDto

interface CoursesRepository {
    suspend fun createCourse(coursesDto: CoursesDto, uid: String): String
    suspend fun deleteCourse(courseId: String)
    suspend fun getCourse(courseId: String): CoursesDto?
    suspend fun enrolledCourses(studentId: String): List<CoursesDto>
    suspend fun teachingCourses(teacherId: String): List<CoursesDto>
    suspend fun updateCourse(courseId: String, coursesDto: CoursesDto)
}