package id.altea.care.view.doctordetail.bottomsheet

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.select.getSelectExtension
import id.altea.care.core.base.BaseBottomSheet
import id.altea.care.core.domain.model.DoctorSchedule
import id.altea.care.core.ext.changeStateButton
import id.altea.care.databinding.BottomsheetScheduleBinding
import id.altea.care.view.doctordetail.item.ScheduleTimeClickEvent
import id.altea.care.view.doctordetail.item.ScheduleTimeItem

class ScheduleBottomSheet(
    private val callback: (DoctorSchedule) -> Unit,
    private val dismissCallback: () -> Unit
) : BaseBottomSheet<BottomsheetScheduleBinding>() {

    private val itemAdapter = ItemAdapter<ScheduleTimeItem>()
    private val fastAdapter = FastAdapter.with(itemAdapter)
    private var selectExtension = fastAdapter.getSelectExtension()
    private lateinit var doctorSchedule: DoctorSchedule

    override fun getUiBinding(): BottomsheetScheduleBinding {
        return BottomsheetScheduleBinding.inflate(cloneLayoutInflater)
    }

    override fun onFirstLaunch(view: View, savedInstanceState: Bundle?) {
        viewBinding?.run {
            scheduleBottomSheetTxtDate.text = arguments?.getString(EXTRA_DATE)
            scheduleBottomSheetBtnNext.setOnClickListener { callback(doctorSchedule)}
        }
        initRecyclerView()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet = (it as BottomSheetDialog)
                .findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            val behavior = BottomSheetBehavior.from(bottomSheet!!)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        return dialog
    }

    private fun initRecyclerView() {
        selectExtension.isSelectable = true
        viewBinding?.scheduleBottomSheetRecyclerview?.run {
            layoutManager = GridLayoutManager(
                requireContext(), 3, GridLayoutManager.VERTICAL, false
            )
            adapter = fastAdapter
        }

        fastAdapter.addEventHook(ScheduleTimeClickEvent {
            doctorSchedule = it
            viewBinding?.scheduleBottomSheetBtnNext?.changeStateButton(true)
        })

        arguments?.getParcelableArray(EXTRA_SCHEDULE)?.map {
            itemAdapter.add(ScheduleTimeItem(it as DoctorSchedule))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dismissCallback()
    }

    companion object {
        private const val EXTRA_SCHEDULE = "ScheduleBottomSheet.Schedules"
        private const val EXTRA_DATE = "ScheduleBottomSheet.Date"
        fun newInstance(
            date: String,
            schedules: List<DoctorSchedule>,
            submitCallback: ((DoctorSchedule) -> Unit) = {},
            dismissCallback: (() -> Unit) = {}
        ): ScheduleBottomSheet {
            return ScheduleBottomSheet(submitCallback, dismissCallback).apply {
                arguments =
                    Bundle().apply {
                        putParcelableArray(EXTRA_SCHEDULE, schedules.toTypedArray())
                        putString(EXTRA_DATE, date)
                    }
            }
        }
    }
}
