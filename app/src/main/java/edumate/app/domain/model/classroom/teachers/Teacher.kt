package edumate.app.domain.model.classroom.teachers

import edumate.app.domain.model.userProfiles.UserProfile

data class Teacher(
    val courseId: String? = null,
    val profile: UserProfile? = null,
    val userId: String? = null,
)
