package app.edumate.data.repository

import app.edumate.domain.repository.PlayRepository
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.tasks.await

class PlayRepositoryImpl(
    private val updateManager: AppUpdateManager,
) : PlayRepository {
    override suspend fun checkUpdateAvailability(): AppUpdateInfo? {
        val updateInfoTask = updateManager.appUpdateInfo
        val updateInfo = updateInfoTask.await()
        val updateAvailable =
            updateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                updateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
        return if (updateAvailable) updateInfo else null
    }
}
