package edumate.app.domain.usecase.classroom.courseWork

import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.data.mapper.toCourseWorkDomainModel
import edumate.app.domain.model.classroom.courseWork.CourseWork
import edumate.app.domain.repository.AuthenticationRepository
import edumate.app.domain.repository.CourseWorkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import edumate.app.R.string as Strings

class GetCourseWorkUseCase
    @Inject
    constructor(
        private val authenticationRepository: AuthenticationRepository,
        private val courseWorkRepository: CourseWorkRepository,
    ) {
        operator fun invoke(
            courseId: String,
            id: String,
        ): Flow<Result<CourseWork>> =
            flow {
                try {
                    emit(Result.Loading())
                    val idToken = authenticationRepository.getIdToken()
                    val courseWork =
                        courseWorkRepository.get(idToken, courseId, id).toCourseWorkDomainModel()
                    emit(Result.Success(courseWork))
                } catch (e: Exception) {
                    emit(Result.Error(UiText.StringResource(Strings.cannot_retrieve_course_work_at_this_time_please_try_again_later)))
                }
            }
    }
