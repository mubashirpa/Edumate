package edumate.app.domain.usecase.course_work

import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.data.remote.mapper.toCourseWork
import edumate.app.domain.model.course_work.CourseWork
import edumate.app.domain.repository.CourseWorkRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetCourseWorksUseCase @Inject constructor(
    private val courseWorkRepository: CourseWorkRepository
) {
    operator fun invoke(courseId: String): Flow<Resource<List<CourseWork>>> = flow {
        try {
            emit(Resource.Loading())
            val courseWorks = courseWorkRepository.courseWorks(courseId).map { it.toCourseWork() }
            emit(Resource.Success(courseWorks))
        } catch (e: Exception) {
            emit(Resource.Error(UiText.DynamicString(e.message!!)))
        }
    }
}