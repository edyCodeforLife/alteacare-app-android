package id.altea.care.view.common.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.core.domain.model.Country
import id.altea.care.core.domain.model.TypeMessage
import id.altea.care.databinding.ItemGeneralSelectableRadioBinding

class SelectableTypeMessageItem(val typeMessage: TypeMessage ) : AbstractBindingItem<ItemGeneralSelectableRadioBinding>() {
    override val type: Int
        get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemGeneralSelectableRadioBinding {
        return ItemGeneralSelectableRadioBinding.inflate(inflater, parent, false)
    }

    override fun bindView(binding: ItemGeneralSelectableRadioBinding, payloads: List<Any>) {
        binding.itemGeneralRadio.isChecked = isSelected
        binding.itemGeneralRadio.text = typeMessage.name
    }
}