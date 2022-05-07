package id.altea.care.view.consultation

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import id.altea.care.core.domain.model.Consultation
import id.altea.care.core.domain.model.Doctor
import id.altea.care.core.domain.model.PatientFamily
import id.altea.care.view.doctordetail.bottomsheet.FamilyMemberListBottomSheet
import id.altea.care.view.login.LoginGuestActivity
import id.altea.care.view.main.MainActivity
import id.altea.care.view.specialistsearch.SpecialistSearchActivity


class ConsultationRouter {

    companion object {
        const val KEY = "KEY"
        const val KEY_CONSULTATION_DATA = "KEY_CONSULTATION_DATA"
        const val EXTRA_PAGE = "EXTRA_PAGE"
        const val KEY_CONSULTATION = "KEY_CONSULTATION"
        const val EXTRA_ORDER_CODE = "Extra.OrderCode"
        const val EXTRA_APPOINTMENT = "Extra.AppointmentId"

        fun openConsultationFragment(context: Context, pageType: PageType, patientFamily: PatientFamily?, consultation: Consultation?): Intent{
            return Intent(context, ConsultationActivity::class.java)
                .putExtra(EXTRA_PAGE, pageType)
                .putExtra(KEY, patientFamily)
                .putExtra(KEY_CONSULTATION_DATA, consultation)
        }

        fun createIntent(
            caller: Context,
            pageType: PageType,
            dataConsultation: Consultation? = null,
            orderCode: String? = null, // set null if call from doctor detail
            appointmentId: Int? = null, // set null if open consultation
        ): Intent {
            return Intent(caller, ConsultationActivity::class.java)
                .putExtra(EXTRA_PAGE, pageType)
                .putExtra(KEY_CONSULTATION_DATA, dataConsultation)
                .putExtra(EXTRA_ORDER_CODE, orderCode)
                .putExtra(EXTRA_APPOINTMENT, appointmentId)

        }
    }

    fun openHome(caller: AppCompatActivity) {
        caller.startActivity(
            MainActivity.createIntent(caller, 0).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
        caller.finish()
    }

    fun openSearchSpecialist(caller: FragmentActivity, specialistId : String?){
        caller.startActivity(SpecialistSearchActivity.createIntent(caller, specialistId, null))
    }

    fun openGuestLogin(activity: FragmentActivity, consultation : Consultation?) {
        activity.startActivity(LoginGuestActivity.createIntent(activity, Doctor(
            "","","","", listOf(),
            consultation?.nameDoctor,consultation?.imgDoctor)
        ))
        activity.finish()
    }

    fun openFamilyMemberListBottomSheet(activity: FragmentActivity, consultation: Consultation? = null, isShowing: Boolean) {
        FamilyMemberListBottomSheet.newInstance(consultation, isShowing).show(activity.supportFragmentManager, "FAMILY_MEMBER_LIST")
    }

}

enum class PageType {
    CONSULTATION, PAYMENT
}