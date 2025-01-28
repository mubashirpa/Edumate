package app.edumate.domain.repository

import app.edumate.data.remote.dto.courseWorks.CourseWorkDto

interface CourseWorkRepository {
    suspend fun getCourseWorks(courseId: String): List<CourseWorkDto>

    suspend fun deleteCourseWork(id: String): CourseWorkDto
}
