package edumate.app.domain.usecase.classroom.teachers

import edumate.app.core.Result
import edumate.app.core.UiText
import edumate.app.data.mapper.toTeacherDomainModel
import edumate.app.domain.model.classroom.teachers.Teacher
import edumate.app.domain.repository.AuthenticationRepository
import edumate.app.domain.repository.TeachersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ListTeachersUseCase
    @Inject
    constructor(
        private val authenticationRepository: AuthenticationRepository,
        private val teachersRepository: TeachersRepository,
    ) {
        operator fun invoke(
            courseId: String,
            pageSize: Int? = 30,
            pageToken: String? = null,
        ): Flow<Result<List<Teacher>>> =
            flow {
                try {
                    emit(Result.Loading())
                    val idToken = authenticationRepository.getIdToken()
                    val teachers =
                        teachersRepository.list(
                            idToken,
                            courseId,
                            pageSize,
                            pageToken,
                        ).teachers?.map { it.toTeacherDomainModel() }
                    emit(Result.Success(teachers))
                } catch (e: Exception) {
                    emit(Result.Error(UiText.DynamicString(e.message!!)))
                }
            }
    }
