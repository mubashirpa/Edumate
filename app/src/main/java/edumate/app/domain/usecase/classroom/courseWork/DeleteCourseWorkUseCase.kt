package edumate.app.domain.usecase.classroom.courseWork

import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.domain.repository.CourseWorkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import edumate.app.R.string as Strings

class DeleteCourseWorkUseCase
    @Inject
    constructor(
        private val courseWorkRepository: CourseWorkRepository,
    ) {
        operator fun invoke(
            courseId: String,
            id: String,
        ): Flow<Result<String>> =
            flow {
                try {
                    emit(Result.Loading())
                    courseWorkRepository.delete(courseId, id)
                    emit(Result.Success(id))
                } catch (e: Exception) {
                    emit(Result.Error(UiText.StringResource(Strings.unable_to_delete_course_work)))
                }
            }
    }
