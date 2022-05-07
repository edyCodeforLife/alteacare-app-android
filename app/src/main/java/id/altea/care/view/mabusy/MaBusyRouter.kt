package id.altea.care.view.mabusy

import androidx.fragment.app.FragmentManager
import id.altea.care.view.cancelstatusorder.CancelStatusActvity
import id.altea.care.view.common.bottomsheet.OptionCancelBottomSheet
import id.altea.care.view.reconsultation.ReconsultationActivity
import id.altea.care.view.transition.bottomsheet.CancelCallBottomSheet

class MaBusyRouter {

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

    fun openCancelStatus(source : MaBusyActivity, status : Boolean?){
        source.startActivity(CancelStatusActvity.createIntent(source,status))
        source.finish()
    }
}