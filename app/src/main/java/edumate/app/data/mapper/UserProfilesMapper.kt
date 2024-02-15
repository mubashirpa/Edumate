package edumate.app.data.mapper

import edumate.app.data.remote.dto.userProfiles.Name
import edumate.app.data.remote.dto.userProfiles.UserProfile
import edumate.app.domain.model.userProfiles.Name as NameDomainModel
import edumate.app.domain.model.userProfiles.UserProfile as UserProfileDomainModel

fun UserProfile.toUserProfileDomainModel(): UserProfileDomainModel {
    return UserProfileDomainModel(
        emailAddress = emailAddress,
        id = id,
        name = name?.toNameDomainModel(),
        photoUrl = photoUrl,
        verified = verified,
    )
}

fun UserProfileDomainModel.toUserProfile(): UserProfile {
    return UserProfile(
        emailAddress = emailAddress,
        id = id,
        name = name?.toName(),
        photoUrl = photoUrl,
        verified = verified,
    )
}

private fun Name.toNameDomainModel(): NameDomainModel {
    return NameDomainModel(
        firstName = firstName,
        fullName = fullName,
        lastName = lastName,
    )
}

private fun NameDomainModel.toName(): Name {
    return Name(
        firstName = firstName,
        fullName = fullName,
        lastName = lastName,
    )
}
