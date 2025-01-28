package app.edumate.data.mapper

import app.edumate.data.remote.dto.user.UserDto
import app.edumate.data.remote.dto.user.UsersDto
import app.edumate.domain.model.member.UserRole
import app.edumate.domain.model.user.User
import app.edumate.domain.model.user.Users

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

fun UsersDto.toUsersDomainModel(): Users =
    Users(
        role = role?.let { enumValueOf<UserRole>(it.name) },
        user = user?.toUserDomainModel(),
    )
