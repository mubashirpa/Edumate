package app.edumate

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi
import app.edumate.core.Constants
import app.edumate.di.appModule
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class EdumateApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@EdumateApplication)
            modules(appModule)
        }

        OneSignal.Debug.logLevel = LogLevel.VERBOSE // TODO: Remove before releasing the app
        OneSignal.initWithContext(this, BuildConfig.ONESIGNAL_APP_ID)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val name = getString(R.string.notification_channel_name_all)
        val description = getString(R.string.notification_channel_description_all)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(Constants.ALL_NOTIFICATIONS_CHANNEL_ID, name, importance)
        channel.description = description

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
