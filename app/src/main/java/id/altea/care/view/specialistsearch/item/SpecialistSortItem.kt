package id.altea.care.view.specialistsearch.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.fastadapter.select.SelectExtension
import id.altea.care.databinding.ItemSingleSimpleSelectedBinding
import id.altea.care.view.specialistsearch.model.SpecialistSortModel
import kotlinx.android.synthetic.main.item_single_simple_selected.view.*

class SpecialistSortItem(
    val sortModel: SpecialistSortModel
) : AbstractBindingItem<ItemSingleSimpleSelectedBinding>() {

    override val type: Int get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemSingleSimpleSelectedBinding {
        return ItemSingleSimpleSelectedBinding.inflate(inflater, parent, false)
    }


    override fun bindView(binding: ItemSingleSimpleSelectedBinding, payloads: List<Any>) {
        binding.itemSampleRadioButton.run {
            text = sortModel.title
            isChecked = sortModel.isSelected
        }
    }

    class SortClickEvent(val invoke: (SpecialistSortModel) -> Unit) : ClickEventHook<SpecialistSortItem>() {
        override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
            return viewHolder.itemView.itemSampleRadioButton
        }

        override fun onClick(
            v: View,
            position: Int,
            fastAdapter: FastAdapter<SpecialistSortItem>,
            item: SpecialistSortItem
        ) {
            if (!item.isSelected) {
                val selectExtension: SelectExtension<SpecialistSortItem> = fastAdapter.requireExtension()
                val selections = selectExtension.selections
                if (selections.isNotEmpty()) {
                    val selectedPosition = selections.iterator().next()
                    selectExtension.deselect()
                    fastAdapter.getItem(selectedPosition)?.let {
                        it.sortModel.isSelected = false
                    }
                    fastAdapter.notifyItemChanged(selectedPosition)
                }
                item.sortModel.isSelected = true
                selectExtension.select(position)
            }
            invoke(item.sortModel)
        }
    }
}