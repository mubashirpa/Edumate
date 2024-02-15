package edumate.app.data.mapper

import edumate.app.data.remote.dto.classroom.students.Student
import edumate.app.domain.model.classroom.students.Student as StudentDomainModel

fun Student.toStudentDomainModel(): StudentDomainModel {
    return StudentDomainModel(
        courseId = courseId,
        profile = profile?.toUserProfileDomainModel(),
        studentWorkFolder = studentWorkFolder?.toDriveFolderDomainModel(),
        userId = userId,
    )
}

fun StudentDomainModel.toStudent(): Student {
    return Student(
        courseId = courseId,
        profile = profile?.toUserProfile(),
        studentWorkFolder = studentWorkFolder?.toDriveFolder(),
        userId = userId,
    )
}
