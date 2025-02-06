package app.edumate.domain.repository

import app.edumate.data.remote.dto.courseWork.CourseWorkTypeDto
import app.edumate.data.remote.dto.studentSubmission.StudentSubmissionDto

interface StudentSubmissionRepository {
    suspend fun getStudentSubmission(
        courseId: String,
        courseWorkId: String,
        userId: String,
        courseWorkType: CourseWorkTypeDto,
    ): StudentSubmissionDto
}
