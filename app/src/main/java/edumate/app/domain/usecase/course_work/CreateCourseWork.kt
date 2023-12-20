package edumate.app.domain.usecase.course_work

import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.data.remote.mapper.toCourseWork
import edumate.app.data.remote.mapper.toCourseWorkDto
import edumate.app.domain.model.course_work.CourseWork
import edumate.app.domain.repository.CourseWorkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import edumate.app.R.string as Strings

class CreateCourseWork
    @Inject
    constructor(
        private val courseWorkRepository: CourseWorkRepository,
    ) {
        operator fun invoke(
            courseId: String,
            courseWork: CourseWork,
        ): Flow<Resource<CourseWork?>> =
            flow {
                try {
                    emit(Resource.Loading())
                    val courseWorkResponse =
                        courseWorkRepository.create(courseId, courseWork.toCourseWorkDto())
                            ?.toCourseWork()
                    emit(Resource.Success(courseWorkResponse))
                } catch (e: Exception) {
                    emit(Resource.Error(UiText.StringResource(Strings.unable_to_create_course_work)))
                }
            }
    }
