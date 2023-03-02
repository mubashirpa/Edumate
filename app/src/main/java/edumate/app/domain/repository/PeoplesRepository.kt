package edumate.app.domain.repository

interface PeoplesRepository {
    suspend fun students(roomId: String)
    suspend fun teachers(roomId: String)
    suspend fun addStudentInRoom(roomId: String, uid: String)
    suspend fun addStudentInUser(uid: String, roomId: String)
    suspend fun addTeacherInRoom(roomId: String, uid: String)
    suspend fun addTeacherInUser(uid: String, roomId: String)
    suspend fun deleteStudentInUser(uid: String, roomId: String)
    suspend fun deleteTeacherInUser(uid: String, roomId: String)
}