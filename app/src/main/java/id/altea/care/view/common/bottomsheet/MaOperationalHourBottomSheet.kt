package id.altea.care.view.common.bottomsheet

import android.app.Activity
import android.app.Dialog
import android.content.Intent
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
import id.altea.care.core.ext.isHtmlText
import id.altea.care.core.helper.util.ConstantIndexMenu
import id.altea.care.databinding.BottomsheetMaOperationalHourBinding
import id.altea.care.view.main.MainActivity

class MaOperationalHourBottomSheet(
    private val operationalHour: String,
    private val isCreateAppointment: Boolean? = false
) : BaseBottomSheet<BottomsheetMaOperationalHourBinding>() {

    override fun getUiBinding(): BottomsheetMaOperationalHourBinding =
        BottomsheetMaOperationalHourBinding.inflate(cloneLayoutInflater)

    override fun onFirstLaunch(view: View, savedInstanceState: Bundle?) {
        viewBinding?.run {
            btnClose.onSingleClick()
                .subscribe {
                    if (isCreateAppointment == true){
                        openMainActivity(requireActivity())
                    }else {
                        dismiss()
                    }
                }.disposedBy(disposable)

            val maMessage = resources.getString(R.string.message_ma_operational_hour)
            txtMaOperationalHour.text = maMessage.replace("time_uniq", operationalHour).isHtmlText()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheet =
                (dialogInterface as BottomSheetDialog).findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
            val behavior = BottomSheetBehavior.from(bottomSheet!!)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.isDraggable = false
        }

        return dialog
    }

    private fun openMainActivity(source: Activity) {
        source.startActivity(MainActivity.createIntent(source, ConstantIndexMenu.INDEX_MENU_MY_CONSULTATION).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        source.finish()
    }

    companion object {
        fun newInstance(
            operationalHour: String,
            isCreateAppointment: Boolean? = false
        ): BottomSheetDialogFragment {
            return MaOperationalHourBottomSheet(operationalHour, isCreateAppointment)
        }
    }
}