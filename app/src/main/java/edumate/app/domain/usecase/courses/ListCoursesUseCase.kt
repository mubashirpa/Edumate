package edumate.app.domain.usecase.courses

import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.data.remote.mapper.toCourses
import edumate.app.domain.model.courses.CourseState
import edumate.app.domain.model.courses.Courses
import edumate.app.domain.repository.CoursesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import edumate.app.R.string as Strings

class ListCoursesUseCase
    @Inject
    constructor(
        private val coursesRepository: CoursesRepository,
    ) {
        operator fun invoke(
            courseStates: List<CourseState> =
                listOf(
                    CourseState.ACTIVE,
                    CourseState.ARCHIVED,
                    CourseState.PROVISIONED,
                    CourseState.DECLINED,
                ),
            pageSize: Int? = null,
            pageToken: String? = null,
            studentId: String? = null,
            teacherId: String? = null,
        ): Flow<Result<Courses>> =
            flow {
                try {
                    emit(Result.Loading())
                    val courses =
                        coursesRepository.list(courseStates, pageSize, pageToken, studentId, teacherId)
                            .toCourses()
                    emit(Result.Success(courses))
                } catch (e: Exception) {
                    emit(Result.Error(UiText.StringResource(Strings.cannot_retrieve_courses_at_this_time_please_try_again_later)))
                }
            }
    }
