package edumate.app.domain.usecase.course_work

import edumate.app.R.string as Strings
import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.data.remote.mapper.toCourseWork
import edumate.app.data.remote.mapper.toCourseWorkDto
import edumate.app.domain.model.course_work.CourseWork
import edumate.app.domain.repository.CourseWorkRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PatchCourseWork @Inject constructor(
    private val courseWorkRepository: CourseWorkRepository
) {
    operator fun invoke(
        courseId: String,
        id: String,
        courseWork: CourseWork
    ): Flow<Resource<CourseWork?>> =
        flow {
            try {
                emit(Resource.Loading())
                val courseWorkResponse =
                    courseWorkRepository.patch(courseId, id, courseWork.toCourseWorkDto())
                        ?.toCourseWork()
                emit(Resource.Success(courseWorkResponse))
            } catch (e: Exception) {
                emit(Resource.Error(UiText.StringResource(Strings.unable_to_patch_course_work)))
            }
        }
}