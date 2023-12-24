package edumate.app.domain.usecase.courses

import edumate.app.core.UiText
import edumate.app.core.utils.ResourceNew
import edumate.app.domain.repository.CoursesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import edumate.app.R.string as Strings

class DeleteCourse
    @Inject
    constructor(
        private val coursesRepository: CoursesRepository,
    ) {
        operator fun invoke(id: String): Flow<ResourceNew<String>> =
            flow {
                try {
                    emit(ResourceNew.Loading())
                    coursesRepository.delete(id)
                    emit(ResourceNew.Success(id))
                } catch (e: Exception) {
                    emit(ResourceNew.Error(UiText.StringResource(Strings.unable_to_delete_course)))
                }
            }
    }
