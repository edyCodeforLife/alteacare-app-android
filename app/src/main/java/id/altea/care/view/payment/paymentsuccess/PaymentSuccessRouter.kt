package id.altea.care.view.payment.paymentsuccess

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import id.altea.care.core.helper.util.ConstantIndexMenu
import id.altea.care.view.main.MainActivity

class PaymentSuccessRouter {

    fun openHome(caller: AppCompatActivity) {
        caller.startActivity(
            MainActivity.createIntent(caller, ConstantIndexMenu.INDEX_MENU_HOME).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
        caller.finish()
    }

    fun openMyConsultation(caller: AppCompatActivity) {
        caller.startActivity(
            MainActivity.createIntent(caller, ConstantIndexMenu.INDEX_MENU_MY_CONSULTATION).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
        caller.finish()
    }
}
