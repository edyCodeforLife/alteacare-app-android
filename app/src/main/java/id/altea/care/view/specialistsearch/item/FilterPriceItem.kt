package id.altea.care.view.specialistsearch.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.databinding.ItemFilterContentBinding
import id.altea.care.view.specialistsearch.model.SpecialistFilterDayModel
import id.altea.care.view.specialistsearch.model.SpecialistFilterModel
import id.altea.care.view.specialistsearch.model.SpecialistFilterPriceModel

class FilterPriceItem(
    val data: SpecialistFilterModel
) : AbstractBindingItem<ItemFilterContentBinding>() {

    override val type: Int get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemFilterContentBinding {
        return ItemFilterContentBinding.inflate(inflater, parent, false)
    }

    override fun bindView(binding: ItemFilterContentBinding, payloads: List<Any>) {
        binding.run {
            when (data) {
                is SpecialistFilterPriceModel -> {
                    itemFilterContentChipText.text = data.content
                    itemFilterContentChipText.isChecked = data.isSelected
                }
                is SpecialistFilterDayModel -> {
                    itemFilterContentChipText.text = data.dayLocale
                    itemFilterContentChipText.isChecked = data.isSelected
                }
            }
        }
    }
}