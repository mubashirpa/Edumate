package app.edumate.domain.usecase.courseWork

import app.edumate.core.Result
import app.edumate.core.utils.execute
import app.edumate.data.mapper.toCourseWorkDomainModel
import app.edumate.domain.model.courseWork.CourseWork
import app.edumate.domain.repository.CourseWorkRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class GetCourseWorksUseCase(
    private val courseWorkRepository: CourseWorkRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    operator fun invoke(courseId: String): Flow<Result<List<CourseWork>>> =
        execute(ioDispatcher) {
            courseWorkRepository.getCourseWorks(courseId).map {
                it.toCourseWorkDomainModel()
            }
        }
}
