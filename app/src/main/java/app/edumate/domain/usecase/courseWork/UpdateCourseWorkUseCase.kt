package app.edumate.domain.usecase.courseWork

import app.edumate.core.Result
import app.edumate.core.utils.execute
import app.edumate.data.mapper.toCourseWorkDomainModel
import app.edumate.data.mapper.toMaterialDto
import app.edumate.data.remote.dto.courseWork.MultipleChoiceQuestionDto
import app.edumate.domain.model.courseWork.CourseWork
import app.edumate.domain.model.material.Material
import app.edumate.domain.repository.CourseWorkRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

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
        execute(ioDispatcher) {
            val multipleChoiceQuestion =
                MultipleChoiceQuestionDto(choices).takeIf { !it.choices.isNullOrEmpty() }
            courseWorkRepository
                .updateCourseWork(
                    id = id,
                    title = title,
                    description = description.takeIf { !it.isNullOrEmpty() },
                    multipleChoiceQuestion = multipleChoiceQuestion,
                    materials =
                        materials
                            ?.map { it.toMaterialDto() }
                            .takeIf { !it.isNullOrEmpty() },
                    maxPoints = maxPoints?.takeIf { it > 0 },
                    dueTime = dueTime,
                ).toCourseWorkDomainModel()
        }
}
