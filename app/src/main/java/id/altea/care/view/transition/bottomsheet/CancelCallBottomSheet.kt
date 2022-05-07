package id.altea.care.view.transition.bottomsheet

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import id.altea.care.core.base.BaseBottomSheet
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.onSingleClick
import id.altea.care.databinding.BottomsheetCancelTransitionBinding
import id.altea.care.view.transition.TransitionRouter

class CancelCallBottomSheet(val submitCallBack :(Boolean) -> Unit,val dismissCallback : (Boolean) -> Unit) : BaseBottomSheet<BottomsheetCancelTransitionBinding>() {

    override fun getUiBinding(): BottomsheetCancelTransitionBinding  = BottomsheetCancelTransitionBinding.inflate(cloneLayoutInflater)

    override fun onFirstLaunch(view: View, savedInstanceState: Bundle?) {
        viewBinding?.run {
            cancelCallTransitionBtnNo.onSingleClick().subscribe {
                dismiss()
                dismissCallback(true)
            }.disposedBy(disposable)

            cancelCallTransitionBtnYes.onSingleClick().subscribe {
                submitCallBack(true)
            }.disposedBy(disposable)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dismissCallback(true)
    }

    companion object{
        fun newInstance(submitCallBack: (Boolean) -> Unit,dismissCallback: (Boolean) -> Unit) : BottomSheetDialogFragment {
            return CancelCallBottomSheet(submitCallBack,dismissCallback)
        }
    }

}