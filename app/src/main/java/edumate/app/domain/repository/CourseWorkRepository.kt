package edumate.app.domain.repository

import edumate.app.data.remote.dto.CourseWorkDto

interface CourseWorkRepository {
    suspend fun create(courseWorkDto: CourseWorkDto): String
    suspend fun delete(courseWorkId: String, courseId: String)
    suspend fun get(courseWorkId: String, courseId: String): CourseWorkDto?
    suspend fun list(courseId: String): List<CourseWorkDto>
    suspend fun update(courseWorkDto: CourseWorkDto, courseWorkId: String)
}