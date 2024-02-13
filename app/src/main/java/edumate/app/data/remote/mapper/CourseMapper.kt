package edumate.app.data.remote.mapper

import edumate.app.data.remote.dto.courses.Course
import edumate.app.data.remote.dto.courses.CoursesDto
import edumate.app.domain.model.courses.Courses
import edumate.app.domain.model.courses.Course as CourseDomainModel

fun CourseDomainModel.toCourse(): Course {
    return Course(
        description = description,
        id = id,
        name = name,
        ownerId = ownerId,
        room = room,
        section = section,
    )
}

fun Course.toCourseDomainModel(): CourseDomainModel {
    return CourseDomainModel(
        description = description,
        id = id,
        name = name,
        ownerId = ownerId,
        room = room,
        section = section,
    )
}

fun CoursesDto.toCourses(): Courses {
    return Courses(
        courses = courses?.map { it.toCourseDomainModel() },
    )
}
