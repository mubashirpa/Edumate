package edumate.app

import android.app.Application
import com.onesignal.OneSignal
import dagger.hilt.android.HiltAndroidApp
import edumate.app.core.Constants

@HiltAndroidApp
class EdumateApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // OneSignal Initialization
        OneSignal.initWithContext(this)
        OneSignal.setAppId(Constants.ONESIGNAL_APP_ID)
    }
}