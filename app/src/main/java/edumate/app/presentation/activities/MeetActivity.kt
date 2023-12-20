package edumate.app.presentation.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.facebook.react.modules.core.PermissionListener
import com.google.android.material.snackbar.Snackbar
import java.net.MalformedURLException
import java.net.URL
import org.jitsi.meet.sdk.BroadcastEvent
import org.jitsi.meet.sdk.BroadcastIntentHelper
import org.jitsi.meet.sdk.JitsiMeetActivityDelegate
import org.jitsi.meet.sdk.JitsiMeetActivityInterface
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.jitsi.meet.sdk.JitsiMeetView
import timber.log.Timber

private const val CAMERA = "android.permission.CAMERA"
private const val RECORD_AUDIO = "android.permission.RECORD_AUDIO"

class MeetActivity : FragmentActivity(), JitsiMeetActivityInterface {

    private var view: JitsiMeetView? = null
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            onBroadcastReceived(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val meetId = intent.getStringExtra("meetingId")
        val subject = intent.getStringExtra("title")
        if (meetId.isNullOrEmpty()) {
            finish()
        }

        view = JitsiMeetView(this)
        val serverURL: URL = try {
            URL("https://meet.jit.si")
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            throw RuntimeException("Invalid server URL!")
        }
        val options: JitsiMeetConferenceOptions = JitsiMeetConferenceOptions.Builder()
            .setServerURL(serverURL)
            .setRoom(meetId)
            .setSubject(subject ?: meetId)
            .setAudioMuted(false)
            .setVideoMuted(false)
            .setAudioOnly(false)
            .setFeatureFlag("car-mode.enabled", false)
            .setFeatureFlag("invite.enabled", false)
            // .setFeatureFlag("pip.enabled", false)
            .setFeatureFlag("security-options.enabled", false)
            .setFeatureFlag("settings.enabled", false)
            .setFeatureFlag("video-share.enabled", false)
            .setFeatureFlag("welcomepage.enabled", false)
            .setConfigOverride("requireDisplayName", true)
            .build()
        view?.join(options)

        setContentView(view)

        registerForBroadcastMessages()

        onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }

    override fun requestPermissions(p0: Array<out String>?, p1: Int, p2: PermissionListener?) {
        val permissions: Array<String>? = p0?.map { it }?.toTypedArray()
        if (!permissions.isNullOrEmpty()) {
            requestPermissionLauncher.launch(permissions)
        }
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
        super.onDestroy()

        view?.dispose()
        view = null

        JitsiMeetActivityDelegate.onHostDestroy(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        JitsiMeetActivityDelegate.onNewIntent(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        JitsiMeetActivityDelegate.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )
    }

    override fun onResume() {
        super.onResume()

        JitsiMeetActivityDelegate.onHostResume(this)
    }

    override fun onStop() {
        super.onStop()

        JitsiMeetActivityDelegate.onHostPause(this)
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            JitsiMeetActivityDelegate.onBackPressed()
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            val contextView = view?.rootView
            it.forEach { (t, u) ->
                when (t) {
                    CAMERA -> {
                        if (!u && contextView != null) {
                            Snackbar.make(
                                contextView,
                                "Permission for camera was denied",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }

                    RECORD_AUDIO -> {
                        if (!u && contextView != null) {
                            Snackbar.make(
                                contextView,
                                "Permission for microphone was denied",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

    private fun registerForBroadcastMessages() {
        val intentFilter = IntentFilter()

        /* This registers for every possible event sent from JitsiMeetSDK
           If only some of the events are needed, the for loop can be replaced
           with individual statements:
           ex:  intentFilter.addAction(BroadcastEvent.Type.AUDIO_MUTED_CHANGED.action);
                intentFilter.addAction(BroadcastEvent.Type.CONFERENCE_TERMINATED.action);
                ... other events
         */
        for (type in BroadcastEvent.Type.entries) {
            intentFilter.addAction(type.action)
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter)
    }

    private fun onBroadcastReceived(intent: Intent?) {
        if (intent != null) {
            val event = BroadcastEvent(intent)
            when (event.type) {
                BroadcastEvent.Type.READY_TO_CLOSE -> {
                    Timber.i("Received event: %s", event.type)
                    finish()
                }

                else -> {
                    Timber.i("Received event: %s", event.type)
                }
            }
        }
    }

    private fun hangUp() {
        val hangupBroadcastIntent: Intent = BroadcastIntentHelper.buildHangUpIntent()
        LocalBroadcastManager.getInstance(this.applicationContext)
            .sendBroadcast(hangupBroadcastIntent)
    }
}