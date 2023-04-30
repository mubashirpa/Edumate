package edumate.app.domain.usecase.courses

import edumate.app.R.string as Strings
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.domain.repository.CoursesRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DeleteCourse @Inject constructor(
    private val coursesRepository: CoursesRepository
) {
    operator fun invoke(id: String): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())
            coursesRepository.delete(id)
            emit(Resource.Success(id))
        } catch (e: Exception) {
            emit(Resource.Error(UiText.StringResource(Strings.unable_to_delete_course)))
        }
    }
}