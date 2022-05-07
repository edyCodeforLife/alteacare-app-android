package id.altea.care.view.address.addresslist.item

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.core.domain.model.UserAddress
import id.altea.care.core.ext.getCompleteAddress
import id.altea.care.databinding.ItemAddressBinding

class ItemAddress(val address: UserAddress) : AbstractBindingItem<ItemAddressBinding>() {
    override val type: Int
        get() = hashCode()

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?): ItemAddressBinding {
        return ItemAddressBinding.inflate(inflater, parent, false)
    }

    fun setPrimary() {
        address.type = "PRIMARY"
    }

    fun unsetPrimary() {
        address.type = "NOT"
    }

    fun isPrimary(): Boolean {
        return address.type == "PRIMARY"
    }

    override fun bindView(binding: ItemAddressBinding, payloads: List<Any>) {
        binding.run {
            itemAddressIsPrimaryAddress.isVisible = isPrimary()
            itemAddressImgMore.isVisible = !isPrimary()
            itemAddressTextAddress.text = address.getCompleteAddress(root.context)
        }
    }
}
