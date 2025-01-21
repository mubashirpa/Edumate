package app.edumate.data.mapper

import app.edumate.data.remote.dto.users.UserDto
import app.edumate.domain.model.User

fun UserDto.toUserDomainModel(): User =
    User(
        avatarUrl = avatarUrl,
        email = email,
        id = id,
        name = name,
    )

fun User.toUserDto(): UserDto =
    UserDto(
        avatarUrl = avatarUrl,
        email = email,
        id = id,
        name = name,
    )
