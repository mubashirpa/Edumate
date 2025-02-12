package app.edumate.data.mapper

import app.edumate.data.remote.dto.member.UserRoleDto
import app.edumate.data.remote.dto.user.UserDto
import app.edumate.data.remote.dto.user.UsersDto
import app.edumate.domain.model.member.UserRole
import app.edumate.domain.model.user.User

fun UserDto.toUserDomainModel(): User =
    User(
        email = email,
        id = id,
        name = name,
        photoUrl = photoUrl,
        role = role?.let { enumValueOf<UserRole>(it.name) },
    )

fun User.toUserDto(): UserDto =
    UserDto(
        email = email,
        id = id,
        name = name,
        photoUrl = photoUrl,
        role = role?.let { enumValueOf<UserRoleDto>(it.name) },
    )

fun UsersDto.toUserDomainModel(): User =
    User(
        email = user?.email,
        id = user?.id,
        name = user?.name,
        photoUrl = user?.photoUrl,
        role = role?.let { enumValueOf<UserRole>(it.name) },
    )
