package edumate.app.data.remote.mapper

import edumate.app.data.remote.dto.UsersDto
import edumate.app.domain.model.User

fun UsersDto.toUser(): User {
    return User(
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

fun User.toUsers(): UsersDto {
    return UsersDto(
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