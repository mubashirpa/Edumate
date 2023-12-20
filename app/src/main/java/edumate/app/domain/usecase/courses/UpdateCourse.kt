package edumate.app.domain.usecase.courses

import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.data.remote.mapper.toCoursesDto
import edumate.app.domain.model.courses.Course
import edumate.app.domain.repository.CoursesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import edumate.app.R.string as Strings

class UpdateCourse
    @Inject
    constructor(
        private val coursesRepository: CoursesRepository,
    ) {
        operator fun invoke(
            id: String,
            course: Course,
        ): Flow<Resource<String>> =
            flow {
                try {
                    emit(Resource.Loading())
                    coursesRepository.update(id, course.toCoursesDto())
                    emit(Resource.Success(id))
                } catch (e: Exception) {
                    emit(Resource.Error(UiText.StringResource(Strings.unable_to_update_course)))
                }
            }
    }
