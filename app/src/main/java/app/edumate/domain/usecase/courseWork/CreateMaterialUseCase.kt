package app.edumate.domain.usecase.courseWork

import app.edumate.R
import app.edumate.core.Constants
import app.edumate.core.Result
import app.edumate.core.UiText
import app.edumate.data.mapper.toCourseWorkDomainModel
import app.edumate.data.mapper.toCourseWorkDto
import app.edumate.domain.model.courseWork.CourseWork
import app.edumate.domain.model.courseWork.CourseWorkType
import app.edumate.domain.model.material.Material
import app.edumate.domain.repository.AuthenticationRepository
import app.edumate.domain.repository.CourseWorkRepository
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.UUID

class CreateMaterialUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val courseWorkRepository: CourseWorkRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    operator fun invoke(
        courseId: String,
        title: String,
        description: String?,
        materials: List<Material>?,
    ): Flow<Result<CourseWork>> =
        flow {
            try {
                emit(Result.Loading())
                authenticationRepository.currentUser()?.id?.let { userId ->
                    val id = UUID.randomUUID().toString()
                    val courseWork =
                        CourseWork(
                            id = id,
                            courseId = courseId,
                            creatorUserId = userId,
                            alternateLink = "${Constants.EDUMATE_BASE_URL}c/$courseId/m/$id/details",
                            description = description.takeIf { !it.isNullOrEmpty() },
                            materials = materials.takeIf { !it.isNullOrEmpty() },
                            title = title,
                            workType = CourseWorkType.MATERIAL,
                        ).toCourseWorkDto()
                    val result =
                        courseWorkRepository.createCourseWork(courseWork).toCourseWorkDomainModel()
                    emit(Result.Success(result))
                } ?: emit(Result.Error(UiText.StringResource(R.string.error_unexpected)))
            } catch (_: RestException) {
                emit(Result.Error(UiText.StringResource(R.string.error_unexpected)))
            } catch (_: HttpRequestTimeoutException) {
                emit(Result.Error(UiText.StringResource(R.string.error_timeout_exception)))
            } catch (_: HttpRequestException) {
                emit(Result.Error(UiText.StringResource(R.string.error_network_exception)))
            } catch (_: Exception) {
                emit(Result.Error(UiText.StringResource(R.string.error_unknown)))
            }
        }.flowOn(ioDispatcher)
}
