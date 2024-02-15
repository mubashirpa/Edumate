package edumate.app.domain.model.classroom.students

import edumate.app.domain.model.classroom.DriveFolder
import edumate.app.domain.model.userProfiles.UserProfile

data class Student(
    val courseId: String? = null,
    val profile: UserProfile? = null,
    val studentWorkFolder: DriveFolder? = null,
    val userId: String? = null,
)
