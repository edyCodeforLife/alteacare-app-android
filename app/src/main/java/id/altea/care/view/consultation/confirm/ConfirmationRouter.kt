package id.altea.care.view.consultation.confirm

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import id.altea.care.core.domain.model.*
import id.altea.care.view.common.bottomsheet.MaOperationalHourBottomSheet
import id.altea.care.view.consultation.ConfirmCallBottomSheet
import id.altea.care.view.doctordetail.bottomsheet.FamilyMemberListBottomSheet
import id.altea.care.view.login.LoginGuestActivity
import id.altea.care.view.transition.TransitionActivity

class ConfirmationRouter {

    fun openTransitionVideoActivity(
        source: Context,
        appointmentId: Int,
        orderCode: String?,
        userProfile: UserProfile,
        infoDetail: InfoDetail?
    ) {
        source.startActivity(
            TransitionActivity.createIntent(
                source,
                appointmentId,
                orderCode,
                userProfile,
                infoDetail
            )
        )
    }

    fun openConfirmCallBottomSheet(
        caller: FragmentManager?,
        submitCallback: (String) -> Unit,
        dismissCallback: () -> Unit
    ) {
        caller?.let {
            ConfirmCallBottomSheet.newInstance(submitCallback, dismissCallback)
                .show(it, "confirm_call")
        }
    }

    fun openGuestLogin(activity: FragmentActivity, consultation : Consultation?) {
        activity.startActivity(
            LoginGuestActivity.createIntent(activity, Doctor(
                doctorName = consultation?.nameDoctor,
                photo =  consultation?.imgDoctor
            )
        ))
        activity.finish()
    }

    fun openFamilyMemberListBottomSheet(activity: FragmentActivity, consultation: Consultation? = null, isShowing: Boolean) {
        FamilyMemberListBottomSheet.newInstance(consultation, isShowing).show(activity.supportFragmentManager, "FAMILY_MEMBER_LIST")
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