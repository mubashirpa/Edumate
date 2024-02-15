package edumate.app.domain.usecase.classroom.courses

import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.domain.repository.CoursesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import edumate.app.R.string as Strings

class DeleteCourseUseCase
    @Inject
    constructor(
        private val coursesRepository: CoursesRepository,
    ) {
        operator fun invoke(id: String): Flow<Result<String>> =
            flow {
                try {
                    emit(Result.Loading())
                    coursesRepository.delete(id)
                    emit(Result.Success(id))
                } catch (e: Exception) {
                    emit(Result.Error(UiText.StringResource(Strings.unable_to_delete_course)))
                }
            }
    }
