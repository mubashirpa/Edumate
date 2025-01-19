package app.edumate.domain.usecase.authentication

import app.edumate.R
import app.edumate.core.Result
import app.edumate.core.UiText
import app.edumate.domain.model.User
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
                        id = it.id,
                        emailAddress = it.email,
                        displayName = it.userMetadata?.get("full_name")?.toString(),
                        photoUrl = it.userMetadata?.get("avatar_url")?.toString(),
                        isVerified =
                            it.userMetadata
                                ?.get("email_verified")
                                ?.toString()
                                ?.toBoolean(),
                    )
                emit(Result.Success(currentUser))
            } ?: emit(Result.Error(UiText.StringResource(R.string.auth_unknown_exception)))
        }
}
