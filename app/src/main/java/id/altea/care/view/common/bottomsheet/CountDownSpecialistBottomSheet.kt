package id.altea.care.view.common.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import id.altea.care.R
import id.altea.care.core.base.BaseBottomSheetVM
import id.altea.care.core.ext.delay
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.onSingleClick
import id.altea.care.databinding.BottomsheetCountdownSpecialistBinding
import id.altea.care.view.consultationdetail.ConsultationDetailSharedVM

class CountDownSpecialistBottomSheet(
    private val dateConsultation: String,
    private val timeMinuteConsultation: String,
    private val countdownTime: Long,
    private val onFinishTimer: (() -> Unit)? = null
) : BaseBottomSheetVM<BottomsheetCountdownSpecialistBinding, ConsultationDetailSharedVM>() {

    override fun getUiBinding(): BottomsheetCountdownSpecialistBinding =
        BottomsheetCountdownSpecialistBinding.inflate(cloneLayoutInflater)

    override fun onFirstLaunch(view: View, savedInstanceState: Bundle?) {
        viewBinding?.run {

            patientDataTxtDate.text = dateConsultation
            patientDataTxtTime.text = timeMinuteConsultation

            imgClose.onSingleClick()
                .subscribe { dismiss() }
                .disposedBy(disposable)

            viewModel?.setupTimer(
                millisInFuture = countdownTime,
                onTick = {  millisUntilFinished ->
                    viewBinding?.txtCountDownTimer?.text = millisUntilFinished
                }, onFinishTimer = {
                    dismiss()
                    disposable.delay(800){
                        onFinishTimer?.invoke()
                    }
                })
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener { btmSheet ->
            val bottomSheet = (btmSheet as BottomSheetDialog).findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
            val behavior = BottomSheetBehavior.from(bottomSheet!!)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.isDraggable = false
        }

        return dialog
    }

    companion object {
        fun newInstance(
            dateConsultation : String,
            timeMinuteConsultation : String,
            countdownTime: Long,
            onFinishTimer: () -> Unit
        ): BottomSheetDialogFragment {
            return CountDownSpecialistBottomSheet(dateConsultation,timeMinuteConsultation,countdownTime, onFinishTimer)
        }
    }

    override fun observeViewModel(viewModel: ConsultationDetailSharedVM) {}

    override fun bindViewModel(): ConsultationDetailSharedVM {
        return ViewModelProvider(
            requireActivity(),
            viewModelFactory
        )[ConsultationDetailSharedVM::class.java]
    }
}