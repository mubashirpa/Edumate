package edumate.app.domain.usecase.students

import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.data.remote.mapper.toUserProfile
import edumate.app.domain.model.user_profiles.UserProfile
import edumate.app.domain.repository.StudentsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ListStudentsUseCase
    @Inject
    constructor(
        private val studentsRepository: StudentsRepository,
    ) {
        operator fun invoke(courseId: String): Flow<Result<List<UserProfile>>> =
            flow {
                try {
                    emit(Result.Loading())
                    val students = studentsRepository.list(courseId).map { it.toUserProfile() }
                    emit(Result.Success(students))
                } catch (e: Exception) {
                    emit(Result.Error(UiText.DynamicString(e.message!!)))
                }
            }
    }
