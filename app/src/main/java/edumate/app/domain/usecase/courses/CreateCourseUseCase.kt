package edumate.app.domain.usecase.courses

import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.data.remote.mapper.toCourse
import edumate.app.data.remote.mapper.toCourseDomainModel
import edumate.app.domain.model.courses.Course
import edumate.app.domain.repository.CoursesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import edumate.app.R.string as Strings

class CreateCourseUseCase
    @Inject
    constructor(
        private val coursesRepository: CoursesRepository,
    ) {
        operator fun invoke(course: Course): Flow<Result<Course?>> =
            flow {
                try {
                    emit(Result.Loading())
                    val courseResponse =
                        coursesRepository.create(course.toCourse())?.toCourseDomainModel()
                    emit(Result.Success(courseResponse))
                } catch (e: Exception) {
                    emit(Result.Error(UiText.StringResource(Strings.unable_to_create_course)))
                }
            }
    }