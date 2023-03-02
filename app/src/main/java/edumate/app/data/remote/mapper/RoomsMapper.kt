package edumate.app.data.remote.mapper

import edumate.app.data.remote.dto.RoomsDto
import edumate.app.domain.model.rooms.Room

fun RoomsDto.toRoom(): Room {
    return Room(
        createdBy,
        creationDate,
        description,
        id,
        link,
        section,
        students,
        subject,
        teachers,
        title
    )
}

fun Room.toRoomsDto(): RoomsDto {
    return RoomsDto(
        createdBy = createdBy,
        creationDate = creationDate,
        description = description,
        id = id,
        link = link,
        section = section,
        students = students,
        subject = subject,
        teachers = teachers,
        title = title
    )
}