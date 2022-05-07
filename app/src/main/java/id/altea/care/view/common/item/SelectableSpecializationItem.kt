package id.altea.care.view.common.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.fastadapter.select.SelectExtension
import id.altea.care.core.domain.model.HospitalResult
import id.altea.care.core.domain.model.Specialization
import id.altea.care.databinding.ItemGeneralSelectableBinding
import kotlinx.android.synthetic.main.item_general_selectable.view.*

class SelectableSpecializationItem (val specialization: Specialization ) :
    AbstractBindingItem<ItemGeneralSelectableBinding>() {
    override val type: Int
        get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemGeneralSelectableBinding {
        return ItemGeneralSelectableBinding.inflate(inflater, parent, false)
    }

    override fun bindView(binding: ItemGeneralSelectableBinding, payloads: List<Any>) {
        binding.itemGeneralCheckbox.isChecked = isSelected
        binding.itemGeneralCheckbox.text = specialization.name
    }


    inner class CheckBoxClickSpecializationEvent(val invoke : (Specialization) -> Unit) : ClickEventHook<SelectableSpecializationItem>() {

        override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
            return viewHolder.itemView.itemGeneralCheckbox
        }

        override fun onClick(
            v: View,
            position: Int,
            fastAdapter: FastAdapter<SelectableSpecializationItem>,
            item: SelectableSpecializationItem
        ) {
            val selectExtension: SelectExtension<SelectableSpecializationItem> = fastAdapter.requireExtension()
            selectExtension.toggleSelection(position)

            invoke(item.specialization)
        }

    }


}
