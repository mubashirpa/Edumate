package edumate.app.domain.usecase.courses

import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.data.remote.mapper.toCourse
import edumate.app.domain.model.courses.Course
import edumate.app.domain.repository.CoursesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import edumate.app.R.string as Strings

class GetCourse
    @Inject
    constructor(
        private val coursesRepository: CoursesRepository,
    ) {
        operator fun invoke(id: String): Flow<Resource<Course?>> =
            flow {
                try {
                    emit(Resource.Loading())
                    val course = coursesRepository.get(id)?.toCourse()
                    emit(Resource.Success(course))
                } catch (e: Exception) {
                    emit(
                        Resource.Error(
                            UiText.StringResource(
                                Strings.cannot_retrieve_course_at_this_time_please_try_again_later,
                            ),
                        ),
                    )
                }
            }
    }
