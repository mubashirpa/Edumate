package edumate.app.domain.usecase.teachers

import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.data.remote.mapper.toUserProfile
import edumate.app.domain.model.user_profiles.UserProfile
import edumate.app.domain.repository.TeachersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ListTeachers
    @Inject
    constructor(
        private val teachersRepository: TeachersRepository,
    ) {
        operator fun invoke(courseId: String): Flow<Resource<List<UserProfile>>> =
            flow {
                try {
                    emit(Resource.Loading())
                    val teachers = teachersRepository.list(courseId).map { it.toUserProfile() }
                    emit(Resource.Success(teachers))
                } catch (e: Exception) {
                    emit(Resource.Error(UiText.DynamicString(e.message!!)))
                }
            }
    }
