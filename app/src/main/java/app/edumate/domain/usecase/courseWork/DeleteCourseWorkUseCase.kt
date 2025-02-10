package app.edumate.domain.usecase.courseWork

import app.edumate.core.Result
import app.edumate.core.utils.execute
import app.edumate.domain.repository.CourseWorkRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class DeleteCourseWorkUseCase(
    private val courseWorkRepository: CourseWorkRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    operator fun invoke(id: String): Flow<Result<Boolean>> =
        execute(ioDispatcher) {
            courseWorkRepository.deleteCourseWork(id)
            true
        }
}
