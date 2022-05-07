package id.altea.care.view.address.addressform.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.fastadapter.select.SelectExtension
import id.altea.care.core.domain.model.SelectedModel
import id.altea.care.core.ext.asBinding
import id.altea.care.databinding.ItemSingleSimpleSelectedBinding

class ItemAddressSingleSelectable(val data: SelectedModel) :
    AbstractBindingItem<ItemSingleSimpleSelectedBinding>() {
    override val type: Int
        get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemSingleSimpleSelectedBinding {
        return ItemSingleSimpleSelectedBinding.inflate(inflater, parent, false)
    }

    override fun bindView(binding: ItemSingleSimpleSelectedBinding, payloads: List<Any>) {
        binding.itemSampleRadioButton.text = data.getTitle()
        binding.itemSampleRadioButton.isChecked = isSelected
    }

    fun setChecked(isChecked: Boolean) {
        this.isSelected = isChecked
        data.isChecked = isChecked
    }
}

class ItemAddressSingleSelectEvent(val invoke: (SelectedModel) -> Unit) :
    ClickEventHook<ItemAddressSingleSelectable>() {
    override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
        return viewHolder.asBinding<ItemSingleSimpleSelectedBinding> { it.itemSampleRadioButton }
    }

    override fun onClick(
        v: View,
        position: Int,
        fastAdapter: FastAdapter<ItemAddressSingleSelectable>,
        item: ItemAddressSingleSelectable
    ) {
        if (!item.isSelected) {
            val selectExtension: SelectExtension<ItemAddressSingleSelectable> =
                fastAdapter.requireExtension()
            val selections = selectExtension.selections
            if (selections.isNotEmpty()) {
                val selectedPosition = selections.iterator().next()
                selectExtension.deselect()
                fastAdapter.notifyItemChanged(selectedPosition)
            }
            selectExtension.select(position)
        }
        invoke(item.data)
    }
}
