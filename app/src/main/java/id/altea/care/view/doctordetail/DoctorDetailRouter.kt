package id.altea.care.view.doctordetail

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import id.altea.care.core.domain.model.*
import id.altea.care.view.common.bottomsheet.DatePickerBottomSheet
import id.altea.care.view.common.bottomsheet.DatePickerType
import id.altea.care.view.common.bottomsheet.MaOperationalHourBottomSheet
import id.altea.care.view.consultation.ConsultationRouter
import id.altea.care.view.consultation.PageType
import id.altea.care.view.doctordetail.bottomsheet.FamilyMemberListBottomSheet
import id.altea.care.view.doctordetail.bottomsheet.ScheduleBottomSheet
import id.altea.care.view.login.LoginGuestActivity
import id.altea.care.view.transition.TransitionActivity

/**
 * Created by trileksono on 10/03/21.
 */
class DoctorDetailRouter {

    fun openConsultationActivity(
        source: DoctorDetailActivity,
        pageType: PageType,
        consultation: Consultation,
    ) {
        source.startActivity(
            ConsultationRouter.createIntent(
                source, pageType,
                consultation
            )
        )
    }

    fun openDateBottomSheet(activity: AppCompatActivity, callback: (String) -> Unit) {
        DatePickerBottomSheet.newInstance(DatePickerType.SCHEDULE, callback)
            .show(activity.supportFragmentManager, "Date")
    }

    fun openScheduleBottomSheet(
        activity: AppCompatActivity,
        date: String,
        schedules: List<DoctorSchedule>,
        callback: (DoctorSchedule) -> Unit
    ) {
        ScheduleBottomSheet.newInstance(date, schedules, callback)
            .show(activity.supportFragmentManager, "schedule")
    }

    fun openGuestLogin(activity: AppCompatActivity, doctor: Doctor) {
        activity.startActivity(LoginGuestActivity.createIntent(activity, doctor))
    }

    fun openTransition(
        activity: AppCompatActivity,
        appointmentId: Int,
        userProfile: UserProfile,
        orderCode: String = "",
        infoDetail: InfoDetail?
    ) {
        activity.startActivity(
            TransitionActivity.createIntent(
                activity,
                appointmentId,
                orderCode,
                userProfile,
                infoDetail
            )
        )
        activity.finish()
    }

    fun openPayment(source: DoctorDetailActivity, consultation: Consultation, appointmentId: Int) {
        source.startActivity(
            ConsultationRouter.createIntent(
                source,
                PageType.PAYMENT,
                consultation,
                appointmentId = appointmentId
            )
        )
        source.finish()
    }

    fun openFamilyMemberListBottomSheet(activity: AppCompatActivity, consultation: Consultation? = null) {
        FamilyMemberListBottomSheet.newInstance(consultation, false).show(activity.supportFragmentManager, "FAMILY_MEMBER_LIST")
    }

    fun openMaOperationalHourBottomSheet(
        fragmentManager: FragmentManager?,
        operationalHour : String
    ) {
        fragmentManager?.let { fragManager ->
            MaOperationalHourBottomSheet
                .newInstance(operationalHour, true)
                .show(fragManager,"ma operational hour")
        }
    }
}
