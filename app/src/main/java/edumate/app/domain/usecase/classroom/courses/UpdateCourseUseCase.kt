package edumate.app.domain.usecase.classroom.courses

import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.data.mapper.toCourse
import edumate.app.domain.model.classroom.courses.Course
import edumate.app.domain.repository.CoursesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import edumate.app.R.string as Strings

class UpdateCourseUseCase
    @Inject
    constructor(
        private val coursesRepository: CoursesRepository,
    ) {
        operator fun invoke(
            id: String,
            course: Course,
        ): Flow<Result<String>> =
            flow {
                try {
                    emit(Result.Loading())
                    coursesRepository.update(id, course.toCourse())
                    emit(Result.Success(id))
                } catch (e: Exception) {
                    emit(Result.Error(UiText.StringResource(Strings.unable_to_update_course)))
                }
            }
    }
