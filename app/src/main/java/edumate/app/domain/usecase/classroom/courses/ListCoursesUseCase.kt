package edumate.app.domain.usecase.classroom.courses

import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.data.mapper.toCourseDomainModel
import edumate.app.domain.model.classroom.courses.Course
import edumate.app.domain.model.classroom.courses.CourseState
import edumate.app.domain.repository.AuthenticationRepository
import edumate.app.domain.repository.CoursesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import edumate.app.R.string as Strings

class ListCoursesUseCase
    @Inject
    constructor(
        private val authenticationRepository: AuthenticationRepository,
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
        ): Flow<Result<List<Course>>> =
            flow {
                try {
                    emit(Result.Loading())
                    val idToken = authenticationRepository.getIdToken()
                    val courses =
                        coursesRepository.list(
                            idToken,
                            courseStates,
                            pageSize,
                            pageToken,
                            studentId,
                            teacherId,
                        ).courses?.map { it.toCourseDomainModel() }
                    emit(Result.Success(courses))
                } catch (e: Exception) {
                    emit(Result.Error(UiText.StringResource(Strings.cannot_retrieve_courses_at_this_time_please_try_again_later)))
                }
            }
    }
