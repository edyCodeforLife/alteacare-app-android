package id.altea.care.view.common.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import id.altea.care.R
import id.altea.care.core.base.BaseBottomSheet
import id.altea.care.core.ext.changeStateButton
import id.altea.care.core.ext.cloneDefaultTheme
import id.altea.care.core.ext.isDateBeforeToday
import id.altea.care.databinding.BottomsheetDatePickerBinding
import kotlinx.android.synthetic.main.bottomsheet_date_picker.*
import timber.log.Timber
import java.util.*

/**
 * Created by trileksono on 03/03/21.
 */
class DatePickerBottomSheet(
    private val submitCallback: (String) -> Unit,
    private val dismissCallback: () -> Unit
) : BaseBottomSheet<BottomsheetDatePickerBinding>() {

    private var selectedDate: String? = null
    private lateinit var dateType: DatePickerType

    override fun getUiBinding(): BottomsheetDatePickerBinding {
        return BottomsheetDatePickerBinding.inflate(cloneLayoutInflater)
    }

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

    override fun onFirstLaunch(view: View, savedInstanceState: Bundle?) {
        dateType = arguments?.getSerializable(EXTRA_DATE_TYPE) as DatePickerType
        val calendar = Calendar.getInstance()
        viewBinding.apply {
            datePickerBottomSheetDate.init(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
            ) { _, year, month, day ->
                val monthString = if (month + 1 < 10) "0${month + 1}" else "${month + 1}"
                val dayString = if (day < 10) "0$day" else "$day"
                selectedDate = "$dayString/$monthString/$year"
                selectedDate?.let { changeStateButton(it) }
            }

            datePickerBtnSelect.setOnClickListener {
                selectedDate?.let { submitCallback(it) }
                dismiss()
            }
        }
    }

    private fun changeStateButton(selectDate: String) {
        when (dateType) {
            DatePickerType.BIRTH_DATE -> datePickerBtnSelect.changeStateButton(true)
            DatePickerType.SCHEDULE -> {
                if (selectDate.isDateBeforeToday()) {
                    datePickerBtnSelect.changeStateButton(false)
                } else {
                    datePickerBtnSelect.changeStateButton(true)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dismissCallback()
    }

    companion object {
        private const val EXTRA_DATE_TYPE = "DatePicker.Type"
        fun newInstance(
            dateType: DatePickerType,
            submitCallback: (String) -> Unit,
            dismissCallback: (() -> Unit) = {}
        ): BottomSheetDialogFragment {
            return DatePickerBottomSheet(submitCallback, dismissCallback).apply {
                val bundle = Bundle()
                bundle.putSerializable(EXTRA_DATE_TYPE, dateType)
                arguments = bundle
            }
        }
    }
}

enum class DatePickerType {
    SCHEDULE, BIRTH_DATE
}
