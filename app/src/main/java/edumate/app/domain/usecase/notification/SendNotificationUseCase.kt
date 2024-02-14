package edumate.app.domain.usecase.notification

import edumate.app.domain.repository.NotificationService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SendNotificationUseCase
    @Inject
    constructor(
        private val notificationService: NotificationService,
    ) {
        private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default

        suspend operator fun invoke(
            title: String,
            description: String,
            userIds: List<String> = emptyList(),
        ) = withContext(defaultDispatcher) {
            if (userIds.isEmpty()) {
                notificationService.send(title, description)
            } else {
                notificationService.send(title, description, userIds)
            }
        }
    }
