package app.edumate.domain.usecase.courseWork

import app.edumate.core.Constants
import app.edumate.core.Result
import app.edumate.core.UnauthenticatedAccessException
import app.edumate.core.utils.execute
import app.edumate.data.mapper.toCourseWorkDomainModel
import app.edumate.data.mapper.toCourseWorkDto
import app.edumate.domain.model.courseWork.CourseWork
import app.edumate.domain.model.courseWork.CourseWorkType
import app.edumate.domain.model.material.Material
import app.edumate.domain.repository.AuthenticationRepository
import app.edumate.domain.repository.CourseWorkRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class CreateAssignmentUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val courseWorkRepository: CourseWorkRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    operator fun invoke(
        courseId: String,
        title: String,
        description: String?,
        materials: List<Material>?,
        maxPoints: Int?,
        dueTime: String?,
        id: String = UUID.randomUUID().toString(),
    ): Flow<Result<CourseWork>> =
        execute(ioDispatcher) {
            val userId =
                authenticationRepository.currentUser()?.id ?: throw UnauthenticatedAccessException()
            val courseWork =
                CourseWork(
                    id = id,
                    courseId = courseId,
                    creatorUserId = userId,
                    alternateLink = "${Constants.EDUMATE_BASE_URL}c/$courseId/a/$id/details",
                    description = description.takeIf { !it.isNullOrEmpty() },
                    dueTime = dueTime,
                    materials = materials.takeIf { !it.isNullOrEmpty() },
                    maxPoints = maxPoints?.takeIf { it > 0 },
                    title = title,
                    workType = CourseWorkType.ASSIGNMENT,
                ).toCourseWorkDto()
            courseWorkRepository.createCourseWork(courseWork).toCourseWorkDomainModel()
        }
}
