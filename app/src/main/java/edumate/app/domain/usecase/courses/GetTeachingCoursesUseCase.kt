package edumate.app.domain.usecase.courses

import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.data.remote.mapper.toCourse
import edumate.app.domain.model.courses.Course
import edumate.app.domain.repository.CoursesRepository
import edumate.app.domain.repository.FirebaseAuthRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetTeachingCoursesUseCase @Inject constructor(
    private val coursesRepository: CoursesRepository,
    private val firebaseAuthRepository: FirebaseAuthRepository
) {
    operator fun invoke(): Flow<Resource<List<Course>>> = flow {
        try {
            emit(Resource.Loading())
            val courses = coursesRepository.teachingCourses(firebaseAuthRepository.currentUserId)
                .map { it.toCourse() }
            emit(Resource.Success(courses))
        } catch (e: Exception) {
            emit(Resource.Error(UiText.DynamicString(e.message!!)))
        }
    }
}