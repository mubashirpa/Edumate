package edumate.app.domain.usecase.students

import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.data.remote.mapper.toUserProfile
import edumate.app.domain.model.user_profiles.UserProfile
import edumate.app.domain.repository.StudentsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ListStudents
    @Inject
    constructor(
        private val studentsRepository: StudentsRepository,
    ) {
        operator fun invoke(courseId: String): Flow<Resource<List<UserProfile>>> =
            flow {
                try {
                    emit(Resource.Loading())
                    val students = studentsRepository.list(courseId).map { it.toUserProfile() }
                    emit(Resource.Success(students))
                } catch (e: Exception) {
                    emit(Resource.Error(UiText.DynamicString(e.message!!)))
                }
            }
    }
