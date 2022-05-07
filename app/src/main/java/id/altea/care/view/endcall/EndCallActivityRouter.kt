package id.altea.care.view.endcall

import android.content.Intent
import id.altea.care.core.helper.util.ConstantIndexMenu
import id.altea.care.view.consultation.ConsultationRouter
import id.altea.care.view.consultation.PageType
import id.altea.care.view.main.MainActivity

class EndCallActivityRouter {

    fun openPayment(
        source: EndCallActivity,
        orderCode: String?,
        appointmentId: Int
    ) {
        source.startActivity(
            ConsultationRouter.createIntent(
                source,
                PageType.PAYMENT,
                null,
                orderCode,
                appointmentId
            )
        )
    }

    fun openMainActivity(caller: EndCallActivity,viewPagarIndex : Int?=0) {
        caller.startActivity(
            MainActivity.createIntent(caller, ConstantIndexMenu.INDEX_MENU_MY_CONSULTATION,viewPagarIndex).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
        caller.finish()
    }

}