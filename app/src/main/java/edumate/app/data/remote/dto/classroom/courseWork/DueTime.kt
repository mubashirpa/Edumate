package edumate.app.data.remote.dto.classroom.courseWork

import kotlinx.serialization.Serializable

@Serializable
data class DueTime(
    val hours: Int? = null,
    val minutes: Int? = null,
    val nanos: Int? = null,
    val seconds: Int? = null,
)
