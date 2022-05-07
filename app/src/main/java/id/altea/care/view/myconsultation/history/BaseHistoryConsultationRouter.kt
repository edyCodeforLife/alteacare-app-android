package id.altea.care.view.myconsultation.history

import android.app.Activity
import androidx.fragment.app.Fragment
import id.altea.care.core.helper.util.ConstantSortType
import id.altea.care.view.common.enums.TypeAppointment
import id.altea.care.view.consultationdetail.ConsultationDetailActivity
import id.altea.care.view.consultationdetail.canceled.ConsultationCancelActivity
import id.altea.care.view.myconsultation.history.bottomsheet.AppointmentSortBottomSheet

object BaseHistoryConsultationRouter {
    fun openCancelDetail(activity: Activity, appointmentId: Int) {
        activity.startActivity(
            ConsultationCancelActivity.createIntent(activity, appointmentId)
        )
    }

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

    fun openSortAppointmentBottomSheet(
        fragment: Fragment,
        sortType: String?,
        callback: (String) -> Unit
    ) {
        AppointmentSortBottomSheet.newInstance(sortType ?: ConstantSortType.SORT_ASC, callback)
            .show(fragment.childFragmentManager, "sort")
    }
}
