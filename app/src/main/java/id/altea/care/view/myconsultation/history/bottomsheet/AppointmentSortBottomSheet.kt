package id.altea.care.view.myconsultation.history.bottomsheet

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import id.altea.care.core.base.BaseBottomSheet
import id.altea.care.core.ext.disposedBy
import id.altea.care.core.ext.onSingleClick
import id.altea.care.core.helper.util.ConstantSortType
import id.altea.care.databinding.BottomsheetSortAppointmentBinding

class AppointmentSortBottomSheet(
    private val sortType: String,
    private val callback: (String) -> Unit,
    private val dismissCallback: () -> Unit
) : BaseBottomSheet<BottomsheetSortAppointmentBinding>() {

    override fun getUiBinding(): BottomsheetSortAppointmentBinding {
        return BottomsheetSortAppointmentBinding.inflate(cloneLayoutInflater)
    }

    override fun onFirstLaunch(view: View, savedInstanceState: Bundle?) {
        initUiListener()
        if (sortType == ConstantSortType.SORT_DESC) {
            viewBinding?.sortAppointmentRButtonNewes?.isChecked = true
        } else {
            viewBinding?.sortAppointmentRButtonOldest?.isChecked = true
        }
    }

    private fun initUiListener() {
        viewBinding?.run {

            sortBottomSheetBtnSelect.onSingleClick()
                .subscribe {
                    viewBinding?.run {
                        if (sortAppointmentRGroup.checkedRadioButtonId == sortAppointmentRButtonNewes.id) {
                            callback(ConstantSortType.SORT_DESC)
                        } else {
                            callback(ConstantSortType.SORT_ASC)
                        }
                    }
                    dismiss()
                }
                .disposedBy(disposable)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dismissCallback()
    }

    companion object {
        fun newInstance(
            sortType: String,
            submitCallback: ((String) -> Unit) = { },
            dismissCallback: (() -> Unit) = { }
        ): BottomSheetDialogFragment {
            return AppointmentSortBottomSheet(sortType, submitCallback, dismissCallback)
        }
    }
}
