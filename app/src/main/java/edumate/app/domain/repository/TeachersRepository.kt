package edumate.app.domain.repository

import edumate.app.data.remote.dto.UserProfileDto

interface TeachersRepository {
    suspend fun addTeacher(courseId: String, teacherId: String): String
    suspend fun deleteTeacher(courseId: String, teacherId: String)
    suspend fun teachers(courseId: String): List<UserProfileDto>
}