package edumate.app.domain.usecase.courses

import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.data.remote.mapper.toCourseDomainModel
import edumate.app.domain.model.courses.Course
import edumate.app.domain.repository.CoursesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import edumate.app.R.string as Strings

class GetCourseUseCase
    @Inject
    constructor(
        private val coursesRepository: CoursesRepository,
    ) {
        operator fun invoke(id: String): Flow<Result<Course?>> =
            flow {
                try {
                    emit(Result.Loading())
                    val course = coursesRepository.get(id)?.toCourseDomainModel()
                    emit(Result.Success(course))
                } catch (e: Exception) {
                    emit(Result.Error(UiText.StringResource(Strings.cannot_retrieve_course_at_this_time_please_try_again_later)))
                }
            }
    }