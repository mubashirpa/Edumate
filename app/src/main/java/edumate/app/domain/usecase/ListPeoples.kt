package edumate.app.domain.usecase

import edumate.app.core.Resource
import edumate.app.core.UiText
import edumate.app.data.remote.mapper.toUserProfile
import edumate.app.domain.model.user_profiles.UserProfile
import edumate.app.domain.repository.StudentsRepository
import edumate.app.domain.repository.TeachersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import edumate.app.R.string as Strings

class ListPeoples
    @Inject
    constructor(
        private val teachersRepository: TeachersRepository,
        private val studentsRepository: StudentsRepository,
    ) {
        operator fun invoke(courseId: String): Flow<Resource<List<UserProfile>>> =
            flow {
                try {
                    emit(Resource.Loading())
                    val teachers = teachersRepository.list(courseId).map { it.toUserProfile() }
                    val students = studentsRepository.list(courseId).map { it.toUserProfile() }
                    val peoples: MutableList<UserProfile> = mutableListOf()
                    peoples.addAll(teachers)
                    peoples.addAll(students)
                    emit(Resource.Success(peoples))
                } catch (e: Exception) {
                    emit(
                        Resource.Error(
                            UiText.StringResource(
                                Strings.cannot_retrieve_peoples_at_this_time_please_try_again_later,
                            ),
                        ),
                    )
                }
            }
    }
