package edumate.app.domain.usecase.course_work

import edumate.app.R.string as Strings
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.domain.repository.CourseWorkRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DeleteCourseWork @Inject constructor(
    private val courseWorkRepository: CourseWorkRepository
) {
    operator fun invoke(courseId: String, id: String): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())
            courseWorkRepository.delete(courseId, id)
            emit(Resource.Success(id))
        } catch (e: Exception) {
            emit(Resource.Error(UiText.StringResource(Strings.unable_to_delete_course_work)))
        }
    }
}