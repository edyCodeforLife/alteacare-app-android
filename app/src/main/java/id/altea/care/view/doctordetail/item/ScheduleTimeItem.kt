package id.altea.care.view.doctordetail.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.fastadapter.select.SelectExtension
import id.altea.care.core.domain.model.DoctorSchedule
import id.altea.care.core.ext.autoSize
import id.altea.care.databinding.ItemScheduleTimeBinding
import kotlinx.android.synthetic.main.item_schedule_time.view.*

class ScheduleTimeItem(
        val doctor: DoctorSchedule
) : AbstractBindingItem<ItemScheduleTimeBinding>() {

    override val type: Int get() = hashCode()

    override fun createBinding(
            inflater: LayoutInflater,
            parent: ViewGroup?
    ): ItemScheduleTimeBinding {
        return ItemScheduleTimeBinding.inflate(inflater, parent, false)
    }

    override fun bindView(binding: ItemScheduleTimeBinding, payloads: List<Any>) {
        binding.itemScheduleRbTime.run {
            text = doctor.schedule
            isChecked = isSelected
        }
    }
}

class ScheduleTimeClickEvent(val invoke: (DoctorSchedule) -> Unit) : ClickEventHook<ScheduleTimeItem>() {
    override fun onBindMany(viewHolder: RecyclerView.ViewHolder): List<View> {
        return listOf(viewHolder.itemView.rootView, viewHolder.itemView.itemScheduleRbTime)
    }

    override fun onClick(
            v: View,
            position: Int,
            fastAdapter: FastAdapter<ScheduleTimeItem>,
            item: ScheduleTimeItem
    ) {
        if (!item.isSelected) {
            val selectExtension: SelectExtension<ScheduleTimeItem> = fastAdapter.requireExtension()
            val selections = selectExtension.selections
            if (selections.isNotEmpty()) {
                val selectedPosition = selections.iterator().next()
                selectExtension.deselect()
                fastAdapter.notifyItemChanged(selectedPosition)
            }
            selectExtension.select(position)
        }
        invoke(item.doctor)
    }
}
