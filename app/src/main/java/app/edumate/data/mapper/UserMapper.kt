package app.edumate.data.mapper

import app.edumate.data.remote.dto.users.UserDto
import app.edumate.domain.model.User

fun UserDto.toUserDomainModel(): User =
    User(
        displayName = displayName,
        emailAddress = emailAddress,
        id = id,
        isVerified = isVerified,
        photoUrl = photoUrl,
    )

fun User.toUserDto(): UserDto =
    UserDto(
        displayName = displayName,
        emailAddress = emailAddress,
        id = id,
        isVerified = isVerified,
        photoUrl = photoUrl,
    )
