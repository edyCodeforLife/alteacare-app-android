package id.altea.care.view.payment.paymentinformation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import id.altea.care.view.main.MainActivity
import id.altea.care.view.payment.paymentsuccess.PaymentSuccessActivity

class PaymentInformationRouter {


    fun openPaymentSuccess(caller: AppCompatActivity, appointmentId: Int) {
        caller.startActivity(PaymentSuccessActivity.createIntent(caller, appointmentId))
        caller.finish()
    }

    fun openHome(caller: AppCompatActivity) {
        caller.startActivity(
            Intent(MainActivity.createIntent(caller)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
        caller.finish()
    }
}
