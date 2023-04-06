package edumate.app.domain.usecase.course_work

import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.data.remote.mapper.toCourseWork
import edumate.app.domain.model.course_work.CourseWork
import edumate.app.domain.repository.CourseWorkRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetCourseWorkUseCase @Inject constructor(
    private val courseWorkRepository: CourseWorkRepository
) {
    operator fun invoke(courseWorkId: String, courseId: String): Flow<Resource<CourseWork?>> =
        flow {
            try {
                emit(Resource.Loading())
                val courseWork = courseWorkRepository.get(courseWorkId, courseId)?.toCourseWork()
                emit(Resource.Success(courseWork))
            } catch (e: Exception) {
                emit(Resource.Error(UiText.DynamicString("${e.message}")))
            }
        }
}