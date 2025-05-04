package app.edumate.domain.usecase

import app.edumate.R
import app.edumate.core.Result
import app.edumate.core.UiText
import app.edumate.domain.repository.PlayRepository
import com.google.android.play.core.appupdate.AppUpdateInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class CheckUpdateUseCase(
    private val playRepository: PlayRepository,
) {
    operator fun invoke(): Flow<Result<AppUpdateInfo>> =
        flow {
            emit(Result.Loading())
            playRepository.checkUpdateAvailability()?.let { updateInfo ->
                emit(Result.Success(updateInfo))
            } ?: run {
                emit(Result.Error(UiText.StringResource(R.string.error_unexpected)))
            }
        }.catch {
            emit(Result.Error(UiText.StringResource(R.string.error_unknown)))
        }
}
