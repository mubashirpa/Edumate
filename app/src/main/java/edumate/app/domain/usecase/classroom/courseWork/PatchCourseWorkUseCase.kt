package edumate.app.domain.usecase.classroom.courseWork

import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.data.mapper.toCourseWork
import edumate.app.data.mapper.toCourseWorkDomainModel
import edumate.app.domain.model.classroom.courseWork.CourseWork
import edumate.app.domain.repository.AuthenticationRepository
import edumate.app.domain.repository.CourseWorkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import edumate.app.R.string as Strings

class PatchCourseWorkUseCase
    @Inject
    constructor(
        private val authenticationRepository: AuthenticationRepository,
        private val courseWorkRepository: CourseWorkRepository,
    ) {
        operator fun invoke(
            courseId: String,
            id: String,
            updateMask: String,
            courseWork: CourseWork,
        ): Flow<Result<CourseWork>> =
            flow {
                try {
                    emit(Result.Loading())
                    val idToken = authenticationRepository.getIdToken()
                    val courseWorkResponse =
                        courseWorkRepository.patch(
                            idToken,
                            courseId,
                            id,
                            updateMask,
                            courseWork.toCourseWork(),
                        ).toCourseWorkDomainModel()
                    emit(Result.Success(courseWorkResponse))
                } catch (e: Exception) {
                    emit(Result.Error(UiText.StringResource(Strings.unable_to_patch_course_work)))
                }
            }
    }