package id.altea.care.view.specialistsearch.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.databinding.ItemFilterActiveBinding
import id.altea.care.view.specialistsearch.model.FilterActiveModel

class FilterActiveItem(
    val filterActive: FilterActiveModel
) : AbstractBindingItem<ItemFilterActiveBinding>() {

    override val type: Int get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemFilterActiveBinding {
        return ItemFilterActiveBinding.inflate(inflater, parent, false)
    }

    override fun bindView(binding: ItemFilterActiveBinding, payloads: List<Any>) {
        binding.itemScheduleRbDays.text = filterActive.view
    }
}
