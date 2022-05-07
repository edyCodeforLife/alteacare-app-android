package id.altea.care.view.consultationdetail

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import id.altea.care.core.base.BaseBottomSheet
import id.altea.care.core.domain.model.ConsultationDoctor
import id.altea.care.core.ext.autoSize
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.loadImage
import id.altea.care.core.ext.onSingleClick
import id.altea.care.databinding.BottomsheetConfirmConsultationBinding
import kotlinx.android.synthetic.main.bottomsheet_confirm_consultation.*

class ConfirmCallSpecialistBottomSheet(
    private val callback: (String) -> Unit,
    private val dismissCallback: () -> Unit,
    private val doctor : ConsultationDoctor?
) : BaseBottomSheet<BottomsheetConfirmConsultationBinding>() {


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

    override fun getUiBinding(): BottomsheetConfirmConsultationBinding = BottomsheetConfirmConsultationBinding.inflate(cloneLayoutInflater)

    override fun onFirstLaunch(view: View, savedInstanceState: Bundle?) {
        viewBinding?.run {
            doctor?.let {
                bottomSheetImgSpecialist.loadImage(it.photo!!)
            }
        }
        dialogBtnDoctor.onSingleClick().subscribe {
            callback("clicked")
        }.disposedBy(disposable)

        dialogBtnClose.onSingleClick().subscribe {
            dismiss()
        }.disposedBy(disposable)

        dialogTextDescriptionSpecialist.autoSize(disposable)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        dismissCallback()
    }

    companion object {
        fun newInstance(
            submitCallback: (String) -> Unit,
            dismissCallback: () -> Unit,
            doctor : ConsultationDoctor?
        ): BottomSheetDialogFragment {
            return ConfirmCallSpecialistBottomSheet(
                submitCallback,
                dismissCallback,
                doctor
            )
        }
    }
}