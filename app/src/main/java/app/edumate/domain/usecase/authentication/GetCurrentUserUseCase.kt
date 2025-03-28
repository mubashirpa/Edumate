package app.edumate.domain.usecase.authentication

import app.edumate.R
import app.edumate.core.Authentication
import app.edumate.core.Result
import app.edumate.core.UiText
import app.edumate.domain.model.user.User
import app.edumate.domain.repository.AuthenticationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetCurrentUserUseCase(
    private val authenticationRepository: AuthenticationRepository,
) {
    operator fun invoke(): Flow<Result<User>> =
        flow {
            emit(Result.Loading())
            authenticationRepository.currentUser()?.let {
                val currentUser =
                    User(
                        email = it.email,
                        id = it.id,
                        name =
                            it.userMetadata
                                ?.get(Authentication.Metadata.NAME)
                                ?.toString()
                                ?.replace("\"", ""),
                        photoUrl =
                            it.userMetadata
                                ?.get(Authentication.Metadata.AVATAR_URL)
                                ?.toString()
                                ?.replace("\"", ""),
                    )
                emit(Result.Success(currentUser))
            } ?: emit(Result.Error(UiText.StringResource(R.string.auth_unknown_exception)))
        }
}
