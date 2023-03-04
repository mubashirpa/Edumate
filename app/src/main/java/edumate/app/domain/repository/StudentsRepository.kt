package edumate.app.domain.repository

interface StudentsRepository {
    suspend fun addStudent(courseId: String, studentId: String): String
    suspend fun deleteStudent(courseId: String, studentId: String)
}