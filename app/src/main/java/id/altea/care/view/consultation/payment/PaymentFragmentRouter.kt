package id.altea.care.view.consultation.payment

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import id.altea.care.core.domain.model.PaymentTypes
import id.altea.care.view.consultation.voucher.VoucherActivity
import id.altea.care.view.consultationdetail.canceled.ConsultationCancelActivity
import id.altea.care.view.payment.paymentmethod.PaymentMethodActivity
import id.altea.care.view.payment.paymentsuccess.PaymentSuccessActivity

class PaymentFragmentRouter {

    fun openPaymentMethod(
        caller: AppCompatActivity,
        paymentList: List<PaymentTypes>,
        appointmentId: Int,
        orderCode: String,
        voucherCode : String?
    ) {
        caller.startActivity(
            PaymentMethodActivity.createIntent(caller, paymentList, appointmentId, orderCode,voucherCode)
        )
    }

    fun openCancelDetail(source: AppCompatActivity, appointmentId: Int) {
        source.startActivity(
            ConsultationCancelActivity.createIntent(source, appointmentId)
        )
        source.finish()
    }

    fun openPaymentSuccess(source: AppCompatActivity, appointmentId: Int) {
        source.startActivity(PaymentSuccessActivity.createIntent(source, appointmentId))
        source.finish()
    }

    fun openVoucherActivity(caller : AppCompatActivity,appointmentId: String,registerResultLauncher: ActivityResultLauncher<Intent>){
        registerResultLauncher.launch(VoucherActivity.createIntent(caller,appointmentId))
    }
}
