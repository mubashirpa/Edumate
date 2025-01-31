package app.edumate.domain.usecase.courseWork

import app.edumate.R
import app.edumate.core.Result
import app.edumate.core.UiText
import app.edumate.data.mapper.toCourseWorkDomainModel
import app.edumate.data.mapper.toMaterialDto
import app.edumate.data.remote.dto.courseWork.MultipleChoiceQuestionDto
import app.edumate.domain.model.courseWork.CourseWork
import app.edumate.domain.model.material.Material
import app.edumate.domain.repository.CourseWorkRepository
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class UpdateCourseWorkUseCase(
    private val courseWorkRepository: CourseWorkRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    operator fun invoke(
        id: String,
        title: String,
        description: String?,
        choices: List<String>?,
        materials: List<Material>?,
        maxPoints: Int?,
        dueTime: String?,
    ): Flow<Result<CourseWork>> =
        flow {
            try {
                emit(Result.Loading())
                val result =
                    courseWorkRepository
                        .updateCourseWork(
                            id = id,
                            title = title,
                            description = description.takeIf { !it.isNullOrEmpty() },
                            multipleChoiceQuestion =
                                when {
                                    choices.isNullOrEmpty() -> null
                                    else -> MultipleChoiceQuestionDto(choices)
                                },
                            materials =
                                materials
                                    ?.map { it.toMaterialDto() }
                                    .takeIf { !it.isNullOrEmpty() },
                            maxPoints = maxPoints?.takeIf { it > 0 },
                            dueTime = dueTime,
                        ).toCourseWorkDomainModel()
                emit(Result.Success(result))
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
