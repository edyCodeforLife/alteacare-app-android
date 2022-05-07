package id.altea.care.view.address.addresslist.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.mikepenz.fastadapter.binding.BindingViewHolder
import id.altea.care.databinding.ItemAddressBtnAddBinding

class ItemAddressBtnAdd(
    val invoke: () -> Unit
) : AbstractBindingItem<ItemAddressBtnAddBinding>() {

    override val type: Int
        get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemAddressBtnAddBinding {
        return ItemAddressBtnAddBinding.inflate(inflater, parent, false)
    }

    override fun getViewHolder(viewBinding: ItemAddressBtnAddBinding): BindingViewHolder<ItemAddressBtnAddBinding> {
        viewBinding.itemAddressBtnAdd.setOnClickListener { invoke() }
        return super.getViewHolder(viewBinding)
    }
}
