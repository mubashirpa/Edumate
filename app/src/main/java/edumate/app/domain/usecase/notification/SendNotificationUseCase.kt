package edumate.app.domain.usecase.notification

import edumate.app.domain.repository.NotificationApiService
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SendNotificationUseCase @Inject constructor(
    private val notificationApiService: NotificationApiService
) {
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
    suspend operator fun invoke(title: String, description: String) =
        withContext(defaultDispatcher) {
            notificationApiService.send(title, description)
        }
}