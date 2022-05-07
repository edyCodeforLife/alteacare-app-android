package id.altea.care.view.myconsultation.ongoing

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import id.altea.care.core.domain.model.Consultation
import id.altea.care.core.domain.model.ConsultationDoctor
import id.altea.care.core.domain.model.InfoDetail
import id.altea.care.core.domain.model.UserProfile
import id.altea.care.view.common.bottomsheet.MaOperationalHourBottomSheet
import id.altea.care.view.common.enums.TypeAppointment
import id.altea.care.view.consultation.ConsultationRouter
import id.altea.care.view.consultation.PageType
import id.altea.care.view.consultationdetail.ConsultationDetailActivity
import id.altea.care.view.payment.paymentinformation.PaymentInformationActivity
import id.altea.care.view.reconsultation.ReconsultationActivity
import id.altea.care.view.transition.TransitionActivity
import id.altea.care.view.transition.specialist.TransitionSpecialistActivity

class OnGoingRouter {
    fun openConsultationDetail(
        activity: Activity,
        typeAppointment: TypeAppointment,
        consultationId: Int
    ) {
        activity.startActivity(
            ConsultationDetailActivity.createIntent(
                activity,
                typeAppointment,
                consultationId
            )
        )
    }

    fun openConsultation(
        actvity: Activity,
        pageType: PageType,
        dataConsultation: Consultation,
        orderCode: String?,
        appointmentId: Int?
    ) {
        actvity.startActivity(
            ConsultationRouter.createIntent(
                actvity,
                pageType,
                dataConsultation,
                orderCode,
                appointmentId
            )
        )
    }

    fun openPaymentInformation(
        activity: AppCompatActivity,
        appointmentId: Int,
        url: String,
        orderCode: String
    ) {
        activity.startActivity(
            PaymentInformationActivity.createIntent(
                activity,
                appointmentId,
                url,
                orderCode
            )
        )
    }

    fun openReconsultationWithMA(
        activity: AppCompatActivity,
        appointmentId: Int,
        orderCode: String,
        profile: UserProfile,
        infoDetail: InfoDetail?
    ) {
        activity.startActivity(
            ReconsultationActivity.createIntent(activity, appointmentId, orderCode, profile,infoDetail)
        )
    }

    fun openCallWithSpecialist(
        activity: AppCompatActivity,
        appointmentId: Int,
        doctor: ConsultationDoctor?,
        infoDetail: InfoDetail?
    ) {
        activity.startActivity(
            TransitionSpecialistActivity.createIntent(activity, appointmentId,doctor,infoDetail)
        )
    }

    fun openMaOperationalHourBottomSheet(
        fragmentManager: FragmentManager?,
        operationalHour : String
    ) {
        fragmentManager?.let { fragManager ->
            MaOperationalHourBottomSheet
                .newInstance(operationalHour)
                .show(fragManager,"ma operational hour")
        }
    }
}
