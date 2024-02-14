package edumate.app.domain.model.notification

import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    val title: String = "",
    val description: String = "",
    val userIds: List<String> = emptyList(),
)
