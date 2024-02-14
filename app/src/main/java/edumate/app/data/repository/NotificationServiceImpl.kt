package edumate.app.data.repository

import edumate.app.core.Server
import edumate.app.domain.model.notification.Notification
import edumate.app.domain.repository.NotificationService
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.appendPathSegments
import io.ktor.http.contentType
import javax.inject.Inject

class NotificationServiceImpl
    @Inject
    constructor(
        private val client: HttpClient,
    ) : NotificationService {
        override suspend fun send(
            title: String,
            description: String,
        ) {
            client.post(Server.API_BASE_URL) {
                url { appendPathSegments(Server.ENDPOINT_NOTIFICATION) }
                contentType(ContentType.Application.Json)
                setBody(Notification(title, description))
            }
        }

        override suspend fun send(
            title: String,
            description: String,
            userIds: List<String>,
        ) {
            client.post(Server.API_BASE_URL) {
                url { appendPathSegments(Server.ENDPOINT_NOTIFICATION) }
                contentType(ContentType.Application.Json)
                setBody(Notification(title, description, userIds))
            }
        }
    }
