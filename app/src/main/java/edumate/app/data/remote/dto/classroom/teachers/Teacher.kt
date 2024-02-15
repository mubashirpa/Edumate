package edumate.app.data.remote.dto.classroom.teachers

import edumate.app.data.remote.dto.userProfiles.UserProfile
import kotlinx.serialization.Serializable

@Serializable
data class Teacher(
    val courseId: String? = null,
    val profile: UserProfile? = null,
    val userId: String? = null,
)
