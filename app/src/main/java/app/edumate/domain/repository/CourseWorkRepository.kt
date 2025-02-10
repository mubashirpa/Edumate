package app.edumate.domain.repository

import app.edumate.data.remote.dto.courseWork.CourseWorkDto
import app.edumate.data.remote.dto.courseWork.MultipleChoiceQuestionDto
import app.edumate.data.remote.dto.material.MaterialDto

interface CourseWorkRepository {
    suspend fun createCourseWork(courseWork: CourseWorkDto): CourseWorkDto

    suspend fun getCourseWorks(courseId: String): List<CourseWorkDto>

    suspend fun getCourseWork(id: String): CourseWorkDto

    suspend fun updateCourseWork(
        id: String,
        title: String,
        description: String?,
        multipleChoiceQuestion: MultipleChoiceQuestionDto?,
        materials: List<MaterialDto>?,
        maxPoints: Int?,
        dueTime: String?,
    ): CourseWorkDto

    suspend fun deleteCourseWork(id: String): CourseWorkDto
}
