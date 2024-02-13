package edumate.app.domain.usecase.teachers

import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.data.remote.mapper.toUserProfile
import edumate.app.domain.model.user_profiles.UserProfile
import edumate.app.domain.repository.TeachersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ListTeachersUseCase
    @Inject
    constructor(
        private val teachersRepository: TeachersRepository,
    ) {
        operator fun invoke(courseId: String): Flow<Result<List<UserProfile>>> =
            flow {
                try {
                    emit(Result.Loading())
                    val teachers = teachersRepository.list(courseId).map { it.toUserProfile() }
                    emit(Result.Success(teachers))
                } catch (e: Exception) {
                    emit(Result.Error(UiText.DynamicString(e.message!!)))
                }
            }
    }
