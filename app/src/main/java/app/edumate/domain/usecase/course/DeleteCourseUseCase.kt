package app.edumate.domain.usecase.course

import app.edumate.core.Result
import app.edumate.core.utils.execute
import app.edumate.domain.repository.CourseRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class DeleteCourseUseCase(
    private val courseRepository: CourseRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    operator fun invoke(id: String): Flow<Result<Boolean>> =
        execute(ioDispatcher) {
            courseRepository.deleteCourse(id)
            true
        }
}
