package id.altea.care.view.transition

import androidx.fragment.app.FragmentManager
import id.altea.care.core.domain.model.InfoDetail
import id.altea.care.core.domain.model.UserProfile
import id.altea.care.core.helper.util.Constant
import id.altea.care.view.cancelstatusorder.CancelStatusActvity
import id.altea.care.view.common.bottomsheet.OptionCancelBottomSheet
import id.altea.care.view.mabusy.MaBusyActivity
import id.altea.care.view.transition.bottomsheet.CancelCallBottomSheet
import id.altea.care.view.videocall.VideoCallActivity

class TransitionRouter {

    fun openVideoCallActivity(
        source: TransitionActivity,
        appointmentId: Int,
        orderCode: String,
        profile: UserProfile,
        infoDetail: InfoDetail?,
    ) {
        source.startActivity(
            VideoCallActivity.createIntent(
                source,
                appointmentId,
                orderCode,
                Constant.EXTRA_CALL_METHOD_MA,
                profile,
                infoDetail
            )
        )
    }

    fun openCancelCallBottomsheet(fragmentManager : FragmentManager?,submitCallBack : (Boolean) -> Unit,dissmissCallback : (Boolean) -> Unit){
        fragmentManager?.let {
            CancelCallBottomSheet.newInstance(submitCallBack,dissmissCallback).show(it,"cancel bottom sheet")
        }
    }

    fun openOptionCallBottomSheet(fragmentManager: FragmentManager?,submitCallBack: (String?) -> Unit,dissmissCallback: (Boolean) -> Unit){
        fragmentManager?.let {
            OptionCancelBottomSheet.newInstance(submitCallBack,dissmissCallback).show(it,"openOptionCallBottomSheet")
        }
    }

    fun openMaBusyActivity(source : TransitionActivity,appointmentId: Int){
        source.startActivity(MaBusyActivity.createIntent(source,appointmentId))
        source.finish()
    }

    fun openCancelStatus(source : TransitionActivity,status : Boolean?){
        source.startActivity(CancelStatusActvity.createIntent(source,status))
        source.finish()
    }

}