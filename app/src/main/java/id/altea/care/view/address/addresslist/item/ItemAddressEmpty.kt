package id.altea.care.view.address.addresslist.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.databinding.ItemAddressEmptyBinding

class ItemAddressEmpty : AbstractBindingItem<ItemAddressEmptyBinding>() {

    override val type: Int
        get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemAddressEmptyBinding {
        return ItemAddressEmptyBinding.inflate(inflater, parent, false)
    }

}
