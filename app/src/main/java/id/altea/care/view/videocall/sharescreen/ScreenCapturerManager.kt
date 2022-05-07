package id.altea.care.view.videocall.sharescreen

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi

class ScreenCapturerManager internal constructor(context: Context) {
    private var mService: ScreenCaptureService? = null
    private var mContext: Context? = null
    private var currentState = State.UNBIND_SERVICE

    /** Defines callbacks for service binding, passed to bindService()  */
    private val connection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            className: ComponentName,
            service: IBinder
        ) {
            // We've bound to ScreenCapturerService, cast the IBinder and get ScreenCapturerService instance
            val binder: ScreenCaptureService.LocalBinder =
                service as ScreenCaptureService.LocalBinder
            mService = binder.service
            currentState = State.BIND_SERVICE
        }

        override fun onServiceDisconnected(arg0: ComponentName) {}
    }

    /**
     * An enum describing the possible states of a ScreenCapturerManager.
     */
    enum class State {
        BIND_SERVICE, START_FOREGROUND, END_FOREGROUND, UNBIND_SERVICE
    }

    private fun bindService() {
        val intent = Intent(mContext, ScreenCaptureService::class.java)
        mContext?.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun startForeground() {
        mService?.startForeground()
        currentState = State.START_FOREGROUND
    }

    fun endForeground() {
        mService?.endForeground()
        currentState = State.END_FOREGROUND
    }

    fun unbindService() {
        mContext?.unbindService(connection)
        currentState = State.UNBIND_SERVICE
        mContext = null
    }

    init {
        mContext = context
        bindService()
    }
}