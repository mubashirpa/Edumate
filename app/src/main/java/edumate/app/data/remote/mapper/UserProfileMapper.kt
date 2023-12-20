package edumate.app.data.remote.mapper

import edumate.app.data.remote.dto.UserProfileDto
import edumate.app.domain.model.user_profiles.UserProfile

fun UserProfileDto.toUserProfile(): UserProfile {
    return UserProfile(
        createdAt,
        displayName,
        emailAddress,
        id,
        photoUrl,
        enrolled,
        teaching,
        verified,
    )
}
