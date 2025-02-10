package app.edumate.domain.usecase.courseWork

import app.edumate.core.Result
import app.edumate.core.utils.execute
import app.edumate.data.mapper.toCourseWorkDomainModel
import app.edumate.domain.model.courseWork.CourseWork
import app.edumate.domain.repository.CourseWorkRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class GetCourseWorkUseCase(
    private val courseWorkRepository: CourseWorkRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    operator fun invoke(id: String): Flow<Result<CourseWork>> =
        execute(ioDispatcher) {
            courseWorkRepository.getCourseWork(id).toCourseWorkDomainModel()
        }
}
