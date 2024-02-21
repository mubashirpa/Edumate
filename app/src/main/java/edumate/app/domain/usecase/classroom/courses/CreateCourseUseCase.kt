package edumate.app.domain.usecase.classroom.courses

import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.data.mapper.toCourse
import edumate.app.data.mapper.toCourseDomainModel
import edumate.app.domain.model.classroom.courses.Course
import edumate.app.domain.repository.CoursesRepository
import edumate.app.domain.repository.FirebaseAuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import edumate.app.R.string as Strings

class CreateCourseUseCase
    @Inject
    constructor(
        firebaseAuthRepository: FirebaseAuthRepository,
        private val coursesRepository: CoursesRepository,
    ) {
        val userId = firebaseAuthRepository.currentUserId

        operator fun invoke(course: Course): Flow<Result<Course>> =
            flow {
                try {
                    emit(Result.Loading())
                    val courseResponse =
                        coursesRepository.create(userId, course.toCourse())?.toCourseDomainModel()
                    emit(Result.Success(courseResponse))
                } catch (e: Exception) {
                    emit(Result.Error(UiText.StringResource(Strings.unable_to_create_course)))
                }
            }
    }
