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

class ListCourses
    @Inject
    constructor(
        private val coursesRepository: CoursesRepository,
    ) {
        operator fun invoke(
            studentId: String? = null,
            teacherId: String? = null,
        ): Flow<Resource<List<Course>>> =
            flow {
                try {
                    emit(Resource.Loading())
                    val courses = coursesRepository.list(studentId, teacherId).map { it.toCourse() }
                    emit(Resource.Success(courses))
                } catch (e: Exception) {
                    emit(
                        Resource.Error(
                            UiText.StringResource(
                                Strings.cannot_retrieve_courses_at_this_time_please_try_again_later,
                            ),
                        ),
                    )
                }
            }
    }
