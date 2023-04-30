package edumate.app.data.remote.mapper

import edumate.app.data.remote.dto.UserProfileDto
import edumate.app.domain.model.user_profiles.UserProfile

fun UserProfileDto.toUser(): UserProfile {
    return UserProfile(
        createdAt,
        displayName,
        emailAddress,
        id,
        photoUrl,
        enrolled,
        teaching,
        verified
    )
}

fun UserProfile.toUsers(): UserProfileDto {
    return UserProfileDto(
        createdAt,
        displayName,
        emailAddress,
        id,
        photoUrl,
        enrolled,
        teaching,
        verified
    )
}