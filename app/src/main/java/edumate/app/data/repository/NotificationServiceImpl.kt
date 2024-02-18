package edumate.app.data.repository

import edumate.app.domain.repository.NotificationService
import io.ktor.client.HttpClient
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
            TODO("Not yet implemented")
        }

        override suspend fun send(
            title: String,
            description: String,
            userIds: List<String>,
        ) {
            TODO("Not yet implemented")
        }
    }
