package id.altea.care.view.doctordetail.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.fastadapter.select.SelectExtension
import id.altea.care.databinding.ItemScheduleDaysBinding
import id.altea.care.view.doctordetail.item.model.ScheduleDayItemModel
import id.altea.care.view.specialistsearch.model.FilterActiveModel
import kotlinx.android.synthetic.main.item_schedule_days.view.*

class ScheduleDaysItem(
    val dayItem: ScheduleDayItemModel
) : AbstractBindingItem<ItemScheduleDaysBinding>() {

    override val type: Int get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemScheduleDaysBinding {
        return ItemScheduleDaysBinding.inflate(inflater, parent, false)
    }

    override fun bindView(binding: ItemScheduleDaysBinding, payloads: List<Any>) {

        binding.itemScheduleRbDays.run {
            text = dayItem.nameOfDay
            isChecked = isSelected
        }
    }
}

class ScheduleDayClickEvent(val invoke: (ScheduleDayItemModel) -> Unit) :
    ClickEventHook<ScheduleDaysItem>() {
    override fun onBindMany(viewHolder: RecyclerView.ViewHolder): List<View> {
        return listOf(viewHolder.itemView.rootView, viewHolder.itemView.itemScheduleRbDays)
    }

    override fun onClick(
        v: View,
        position: Int,
        fastAdapter: FastAdapter<ScheduleDaysItem>,
        item: ScheduleDaysItem
    ) {
        if (!item.isSelected) {
            val selectExtension: SelectExtension<ScheduleDaysItem> = fastAdapter.requireExtension()
            val selections = selectExtension.selections
            if (selections.isNotEmpty()) {
                val selectedPosition = selections.iterator().next()
                selectExtension.deselect()
                fastAdapter.notifyItemChanged(selectedPosition)
            }
            selectExtension.select(position)
        }
        invoke(item.dayItem)
    }
}
