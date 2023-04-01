package edumate.app.domain.usecase.course_work

import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.data.remote.mapper.toCourseWorkDto
import edumate.app.domain.model.course_work.CourseWork
import edumate.app.domain.repository.CourseWorkRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CreateCourseWorkUseCase @Inject constructor(
    private val courseWorkRepository: CourseWorkRepository
) {
    operator fun invoke(courseWork: CourseWork): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())
            val courseWorkId = courseWorkRepository.create(courseWork.toCourseWorkDto())
            emit(Resource.Success(courseWorkId))
        } catch (e: Exception) {
            emit(Resource.Error(UiText.DynamicString("${e.message}")))
        }
    }
}