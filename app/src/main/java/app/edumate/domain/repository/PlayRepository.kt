package app.edumate.domain.repository

import com.google.android.play.core.appupdate.AppUpdateInfo

interface PlayRepository {
    suspend fun checkUpdateAvailability(): AppUpdateInfo?
}
