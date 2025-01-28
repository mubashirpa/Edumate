package app.edumate.data.mapper

import app.edumate.data.remote.dto.users.UserDto
import app.edumate.data.remote.dto.users.UsersDto
import app.edumate.domain.model.member.UserRole
import app.edumate.domain.model.users.User
import app.edumate.domain.model.users.Users

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
