package edumate.app.domain.repository

interface NotificationService {
    /**
     * Sends a notification to all users.
     * @param title The title of the notification.
     * @param description The description of the notification.
     */
    suspend fun send(
        title: String,
        description: String,
    )

    /**
     * Sends a notification to specified users.
     * @param title The title of the notification.
     * @param description The description of the notification.
     * @param userIds The IDs of users to whom the notification should be sent.
     */
    suspend fun send(
        title: String,
        description: String,
        userIds: List<String>,
    )
}
