package edumate.app.data.mapper

import edumate.app.core.utils.enumValueOf
import edumate.app.data.remote.dto.classroom.courses.Course
import edumate.app.domain.model.classroom.courses.Course as CourseDomainModel

fun CourseDomainModel.toCourse(): Course {
    return Course(
        alternateLink = alternateLink,
        courseState = enumValueOf(courseState?.name),
        creationTime = creationTime,
        description = description,
        id = id,
        name = name,
        ownerId = ownerId,
        photoUrl = photoUrl,
        room = room,
        section = section,
        subject = subject,
        updateTime = updateTime,
    )
}

fun Course.toCourseDomainModel(): CourseDomainModel {
    return CourseDomainModel(
        alternateLink = alternateLink,
        courseState = enumValueOf(courseState?.name),
        creationTime = creationTime,
        description = description,
        id = id,
        name = name,
        ownerId = ownerId,
        photoUrl = photoUrl,
        room = room,
        section = section,
        subject = subject,
        updateTime = updateTime,
    )
}
