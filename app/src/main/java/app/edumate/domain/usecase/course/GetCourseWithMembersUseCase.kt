package app.edumate.domain.usecase.course

import app.edumate.core.Result
import app.edumate.core.utils.execute
import app.edumate.data.mapper.toCourseWithMembersDomainModel
import app.edumate.domain.model.course.CourseWithMembers
import app.edumate.domain.repository.CourseRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class GetCourseWithMembersUseCase(
    private val courseRepository: CourseRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    operator fun invoke(id: String): Flow<Result<CourseWithMembers>> =
        execute(ioDispatcher) {
            courseRepository.getCourseWithMembers(id).toCourseWithMembersDomainModel()
        }
}
