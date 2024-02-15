package edumate.app.data.remote.dto.classroom.students

import edumate.app.data.remote.dto.classroom.DriveFolder
import edumate.app.data.remote.dto.userProfiles.UserProfile
import kotlinx.serialization.Serializable

@Serializable
data class Student(
    val courseId: String? = null,
    val profile: UserProfile? = null,
    val studentWorkFolder: DriveFolder? = null,
    val userId: String? = null,
)
