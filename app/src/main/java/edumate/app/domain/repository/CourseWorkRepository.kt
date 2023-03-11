package edumate.app.domain.repository

import edumate.app.data.remote.dto.CourseWorkDto

interface CourseWorkRepository {
    suspend fun courseWorks(courseId: String): List<CourseWorkDto>
}