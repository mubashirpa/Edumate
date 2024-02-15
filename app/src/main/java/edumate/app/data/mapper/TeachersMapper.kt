package edumate.app.data.mapper

import edumate.app.data.remote.dto.classroom.teachers.Teacher
import edumate.app.domain.model.classroom.teachers.Teacher as TeacherDomainModel

fun Teacher.toTeacherDomainModel(): TeacherDomainModel {
    return TeacherDomainModel(
        courseId = courseId,
        profile = profile?.toUserProfileDomainModel(),
        userId = userId,
    )
}

fun TeacherDomainModel.toTeacher(): Teacher {
    return Teacher(
        courseId = courseId,
        profile = profile?.toUserProfile(),
        userId = userId,
    )
}
