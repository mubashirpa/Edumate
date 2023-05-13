package edumate.app.domain.repository

interface NotificationApiService {

    suspend fun send(title: String, description: String)

    suspend fun send(title: String, description: String, userIds: List<String>)
}