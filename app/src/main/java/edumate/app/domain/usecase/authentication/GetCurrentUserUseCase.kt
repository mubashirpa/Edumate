package edumate.app.domain.usecase.authentication

import edumate.app.data.mapper.toUserProfileDomainModel
import edumate.app.domain.model.userProfiles.UserProfile
import edumate.app.domain.repository.AuthenticationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCurrentUserUseCase
    @Inject
    constructor(
        private val repository: AuthenticationRepository,
    ) {
        operator fun invoke(): Flow<UserProfile> =
            repository.currentUser.map {
                it.toUserProfileDomainModel()
            }
    }
