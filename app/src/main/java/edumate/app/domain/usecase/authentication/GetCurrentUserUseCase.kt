package edumate.app.domain.usecase.authentication

import edumate.app.data.mapper.toUserProfileDomainModel
import edumate.app.domain.model.userProfiles.UserProfile
import edumate.app.domain.repository.AuthenticationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCurrentUserUseCase
    @Inject
    constructor(
        private val repository: AuthenticationRepository,
    ) {
        operator fun invoke(): Flow<UserProfile> =
            flow {
                repository.currentUser.collectLatest { it.toUserProfileDomainModel() }
            }
    }
