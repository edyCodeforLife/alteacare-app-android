package id.altea.care.view.payment.paymentmethod

import androidx.appcompat.app.AppCompatActivity
import id.altea.care.view.payment.paymentinformation.PaymentInformationActivity
import id.altea.care.view.payment.paymentsuccess.PaymentSuccessActivity

class PaymentMethodRouter {
    fun openPaymentInformation(
        source: AppCompatActivity,
        appointmentId: Int,
        url: String,
        orderCode: String
    ) {
        source.startActivity(
            PaymentInformationActivity.createIntent(
                source,
                appointmentId,
                url,
                orderCode
            )
        )
    }

    fun openPaymentSuccess(source: AppCompatActivity, appointmentId: Int) {
        source.startActivity(PaymentSuccessActivity.createIntent(source, appointmentId))
        source.finish()
    }
}
