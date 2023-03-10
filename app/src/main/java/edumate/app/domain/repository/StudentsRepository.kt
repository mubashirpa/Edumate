package edumate.app.domain.repository

import edumate.app.data.remote.dto.UsersDto

interface StudentsRepository {
    suspend fun addStudent(courseId: String, studentId: String): String
    suspend fun deleteStudent(courseId: String, studentId: String)
    suspend fun students(courseId: String): List<UsersDto>
}