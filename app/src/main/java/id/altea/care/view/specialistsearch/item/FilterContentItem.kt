package id.altea.care.view.specialistsearch.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.databinding.ItemFilterContentBinding
import id.altea.care.view.specialistsearch.model.SpecialistFilterHospitalModel
import id.altea.care.view.specialistsearch.model.SpecialistFilterModel
import id.altea.care.view.specialistsearch.model.SpecialistFilterSpecializationModel

class FilterContentItem(
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
        when (data) {
            is SpecialistFilterSpecializationModel -> {
                binding.itemFilterContentChipText.text = data.name
                binding.itemFilterContentChipText.isChecked = data.isSelected
            }
            is SpecialistFilterHospitalModel -> {
                binding.itemFilterContentChipText.text = data.name
                binding.itemFilterContentChipText.isChecked = data.isSelected
            }
        }
    }
}