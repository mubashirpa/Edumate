package edumate.app.domain.usecase.notification

import edumate.app.domain.repository.NotificationApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SendNotificationUseCase
    @Inject
    constructor(
        private val notificationApiService: NotificationApiService,
    ) {
        private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default

        suspend operator fun invoke(
            title: String,
            description: String,
            userIds: List<String> = emptyList(),
        ) = withContext(defaultDispatcher) {
            if (userIds.isEmpty()) {
                notificationApiService.send(title, description)
            } else {
                notificationApiService.send(title, description, userIds)
            }
        }
    }
