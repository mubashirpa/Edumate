package edumate.app.data.repository

import edumate.app.core.Constants
import edumate.app.domain.repository.NotificationApiService
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import javax.inject.Inject

class NotificationApiServiceImpl @Inject constructor(
    private val client: HttpClient
) : NotificationApiService {

    override suspend fun send(title: String, description: String) {
        try {
            client.get(Constants.NOTIFICATION_SERVER_URL) {
                parameter("title", title)
                parameter("description", description)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}