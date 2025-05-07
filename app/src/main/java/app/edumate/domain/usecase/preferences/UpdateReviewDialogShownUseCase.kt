package app.edumate.domain.usecase.preferences

import app.edumate.R
import app.edumate.core.Result
import app.edumate.core.UiText
import app.edumate.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class UpdateReviewDialogShownUseCase(
    private val userPreferencesRepository: UserPreferencesRepository,
) {
    operator fun invoke(lastReviewShownAt: Long): Flow<Result<Boolean>> =
        flow {
            emit(Result.Loading())
            userPreferencesRepository.updateReviewDialogShownTime(lastReviewShownAt)
            emit(Result.Success(true))
        }.catch {
            emit(Result.Error(UiText.StringResource(R.string.error_unknown)))
        }
}
