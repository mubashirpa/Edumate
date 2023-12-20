package edumate.app.data.repository

import android.util.Log
import edumate.app.core.Constants
import edumate.app.domain.repository.NotificationApiService
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import javax.inject.Inject

class NotificationApiServiceImpl
    @Inject
    constructor(
        private val client: HttpClient,
    ) : NotificationApiService {
        override suspend fun send(
            title: String,
            description: String,
        ) {
            try {
                val response: HttpResponse =
                    client.post(Constants.NOTIFICATION_SERVER_URL) {
                        contentType(ContentType.Application.Json)
                        setBody(NotificationRequest(title, description, listOf("All")))
                    }
                Log.d(TAG, response.status.description)
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString(), e)
            }
        }

        override suspend fun send(
            title: String,
            description: String,
            userIds: List<String>,
        ) {
            try {
                val response: HttpResponse =
                    client.post(Constants.NOTIFICATION_SERVER_URL) {
                        contentType(ContentType.Application.Json)
                        setBody(NotificationRequest(title, description, userIds))
                    }
                Log.d(TAG, response.status.description)
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString(), e)
            }
        }

        companion object {
            private val TAG = NotificationApiServiceImpl::class.java.simpleName
        }
    }

@Serializable
data class NotificationRequest(
    val title: String,
    val description: String,
    val userIds: List<String>,
)
