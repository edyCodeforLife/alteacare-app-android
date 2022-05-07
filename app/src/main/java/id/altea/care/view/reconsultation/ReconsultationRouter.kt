package id.altea.care.view.reconsultation

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import id.altea.care.core.domain.model.InfoDetail
import id.altea.care.core.domain.model.UserProfile
import id.altea.care.view.cancelstatusorder.CancelStatusActvity
import id.altea.care.view.common.bottomsheet.OptionCancelBottomSheet
import id.altea.care.view.transition.TransitionActivity
import id.altea.care.view.transition.bottomsheet.CancelCallBottomSheet

class ReconsultationRouter {
    fun openCallWithMA(
        activity: AppCompatActivity,
        appointmentId: Int,
        orderCode: String,
        profile: UserProfile,
        infoDetail: InfoDetail?
    ) {
        activity.startActivity(
            TransitionActivity.createIntent(activity, appointmentId, orderCode, profile,infoDetail)
        )
    }

    fun openCancelCallBottomsheet(fragmentManager : FragmentManager?, submitCallBack : (Boolean) -> Unit, dissmissCallback : (Boolean) -> Unit){
        fragmentManager?.let {
            CancelCallBottomSheet.newInstance(submitCallBack,dissmissCallback).show(it,"cancel bottom sheet")
        }
    }

    fun openOptionCallBottomSheet(fragmentManager: FragmentManager?, submitCallBack: (String?) -> Unit, dissmissCallback: (Boolean) -> Unit){
        fragmentManager?.let {
            OptionCancelBottomSheet.newInstance(submitCallBack,dissmissCallback).show(it,"openOptionCallBottomSheet")
        }
    }

    fun openCancelStatus(source : ReconsultationActivity, status : Boolean?){
        source.startActivity(CancelStatusActvity.createIntent(source,status))
        source.finish()
    }
}