package id.altea.care.view.account

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import id.altea.care.R
import id.altea.care.core.base.BaseBottomSheet
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.onSingleClick
import id.altea.care.databinding.BottomsheetChangePersonalDataBinding
import id.altea.care.databinding.BottomsheetConfirmantionGpBinding
import kotlinx.android.synthetic.main.bottomsheet_change_personal_data.*
import kotlinx.android.synthetic.main.bottomsheet_confirmantion_gp.*

class ChangePersonalBottomSheet(
    private val callback: (String) -> Unit,
    private val dismissCallback: () -> Unit,
    private val state: State
) : BaseBottomSheet<BottomsheetChangePersonalDataBinding>() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet =
                (it as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            val behavior = BottomSheetBehavior.from(bottomSheet!!)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.isDraggable = false
        }

        return dialog
    }

    override fun getUiBinding(): BottomsheetChangePersonalDataBinding = BottomsheetChangePersonalDataBinding.inflate(cloneLayoutInflater)

    override fun onFirstLaunch(view: View, savedInstanceState: Bundle?) {
        dialogBtnChangeProfile.onSingleClick().subscribe {
            callback("clicked")
        }.disposedBy(disposable)

        when(state){
            State.FAMILY ->{
                viewBinding?.textView12?.text = getString(R.string.str_change_family)
                viewBinding?.textView13?.text = getString(R.string.str_description_change_family)
            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        dismissCallback()
    }

    companion object {
        fun newInstance(
            submitCallback: (String) -> Unit,
            dismissCallback: () -> Unit,
            state : State
        ): BottomSheetDialogFragment {
            return ChangePersonalBottomSheet(
                submitCallback,
                dismissCallback,
                state
            )
        }
    }

    enum class State {
        GENERAL,FAMILY
    }
}