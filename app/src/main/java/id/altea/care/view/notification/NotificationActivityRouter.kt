package id.altea.care.view.notification

import android.content.Context
import android.content.Intent

class NotificationActivityRouter {

    companion object {
        fun createIntent(caller: Context): Intent {
            return Intent(caller, NotificationActivity::class.java)
        }
    }

}
