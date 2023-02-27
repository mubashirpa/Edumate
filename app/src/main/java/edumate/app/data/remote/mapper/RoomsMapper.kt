package edumate.app.data.remote.mapper

import edumate.app.data.remote.dto.RoomsDto
import edumate.app.domain.model.Room

fun RoomsDto.toRoom(): Room {
    return Room(
        creationDate,
        description,
        id,
        link,
        members,
        section,
        subject,
        title
    )
}

fun Room.toRoomsDto(): RoomsDto {
    return RoomsDto(
        creationDate = creationDate,
        description = description,
        id = id,
        link = link,
        members = members,
        section = section,
        subject = subject,
        title = title
    )
}