package edumate.app.domain.usecase.classroom.courseWork

import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.data.mapper.toCourseWorkDomainModel
import edumate.app.domain.model.classroom.courseWork.CourseWork
import edumate.app.domain.model.classroom.courseWork.CourseWorkState
import edumate.app.domain.repository.CourseWorkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import edumate.app.R.string as Strings

class ListCourseWorksUseCase
    @Inject
    constructor(
        private val courseWorkRepository: CourseWorkRepository,
    ) {
        operator fun invoke(
            courseId: String,
            courseWorkStates: List<CourseWorkState>? = listOf(CourseWorkState.PUBLISHED),
            orderBy: String? = "updateTime desc",
            pageSize: Int? = null,
            pageToken: String? = null,
        ): Flow<Result<List<CourseWork>>> =
            flow {
                try {
                    emit(Result.Loading())
                    val courseWorks =
                        courseWorkRepository.list(
                            courseId,
                            courseWorkStates?.map { enumValueOf(it.name) },
                            orderBy,
                            pageSize,
                            pageToken,
                        ).courseWork?.map { it.toCourseWorkDomainModel() }
                    emit(Result.Success(courseWorks))
                } catch (e: Exception) {
                    emit(Result.Error(UiText.StringResource(Strings.cannot_retrieve_course_works_at_this_time_please_try_again_later)))
                }
            }
    }
