package id.altea.care.view.deeplink

import android.app.Activity
import id.altea.care.core.helper.util.ConstantIndexMenu
import id.altea.care.view.account.termandcondition.TermConditionAccountRouter
import id.altea.care.view.common.enums.TypeAppointment
import id.altea.care.view.consultation.ConsultationRouter
import id.altea.care.view.consultation.PageType
import id.altea.care.view.consultationdetail.ConsultationDetailActivity
import id.altea.care.view.consultationdetail.canceled.ConsultationCancelActivity
import id.altea.care.view.doctordetail.DoctorDetailActivity
import id.altea.care.view.main.MainActivity
import id.altea.care.view.payment.paymentsuccess.PaymentSuccessActivity
import id.altea.care.view.promotion.detail.PromotionDetailActivity
import id.altea.care.view.promotion.group.PromotionGroupActivity
import id.altea.care.view.promotion.teleconsultation.PromotionTeleconsultationActivity
import id.altea.care.view.register.contactinfo.RegisterContactRouter
import id.altea.care.view.register.contactinfo.RegisterContactState
import id.altea.care.view.specialistsearch.SpecialistSearchActivity

class DeepLinkRouter {

    fun openPaymentPage(source: Activity, appointmentId: Int) {
        source.startActivity(
            ConsultationRouter.createIntent(source, PageType.PAYMENT, appointmentId = appointmentId)
        )
    }

    fun openPaymentSuccess(source: Activity, appointmentId: Int) {
        source.startActivity(PaymentSuccessActivity.createIntent(source, appointmentId))
    }

    fun openDetailAppointmentCancel(source: Activity, appointmentId: Int) {
        source.startActivity(ConsultationCancelActivity.createIntent(source, appointmentId))
    }

    fun openDetailAppointment(
        source: Activity,
        typeAppointment: TypeAppointment? = null,
        appointmentId: Int?,
        tabId: Int? = 0
    ) {
        source.startActivity(
            ConsultationDetailActivity.createIntent(
                source,
                typeAppointment,
                appointmentId,
                tabId
            )
        )
    }

    fun openDetailDoctor(source: Activity, doctorId: String) {
        source.startActivity(
            DoctorDetailActivity.createIntent(
                source,
                doctorId = doctorId
            )
        )

    }

    fun openMyConsultation(
        source: Activity,
        viewPagerIndex: Int? = 0,
        dateMyConsultation: String? = ""
    ) {
        source.startActivity(
            MainActivity.createIntent(
                source,
                ConstantIndexMenu.INDEX_MENU_MY_CONSULTATION,
                viewPagerIndex,
                dateMyConsultation
            )
        )
    }

    fun openRegistration(source: Activity) {
        source.startActivity(
            RegisterContactRouter.createIntent(
                source, null, null,
                RegisterContactState.PAGE_REGISTER, ""
            )
        )
    }

    fun openAccount(source: Activity) {
        source.startActivity(
            MainActivity.createIntent(
                source,
                ConstantIndexMenu.INDEX_MENU_ACCOUNT,
            )
        )
    }

    fun openTermsAndCondition(source: Activity) {
        source.startActivity(
            TermConditionAccountRouter.createIntent(source)
        )
    }

    fun openPromotionListGroup(source: Activity) {
        source.startActivity(PromotionGroupActivity.createIntent(source))
    }

    fun openPromotionTeleconsultation(source: Activity, promotionType: String) {
        source.startActivity(
            PromotionTeleconsultationActivity.createIntent(
                source,
                promotionType
            )
        )
    }

    fun openPromotionDetail(source: Activity, promotionId: Int) {
        source.startActivity(PromotionDetailActivity.createIntent(source, promotionId))
    }

    fun openMainActivity(source: Activity) {
        source.startActivity(
            MainActivity.createIntent(
                source,
                ConstantIndexMenu.INDEX_MENU_HOME
            )
        )
    }


    fun openSearchDoctorSpecialist(
        source: Activity,
        specialistIds: Array<String>?,
        hospitalIds: Array<String>?
    ) {
        source.startActivity(
            SpecialistSearchActivity.createIntent(
                source,
                specialistIds = specialistIds,
                hospitalIds = hospitalIds
            )
        )
    }

    fun openDoctorSpecialist(source: Activity) {
        source.startActivity(
            MainActivity.createIntent(
                source,
                ConstantIndexMenu.INDEX_MENU_SPECIALIST
            )
        )
    }
}
