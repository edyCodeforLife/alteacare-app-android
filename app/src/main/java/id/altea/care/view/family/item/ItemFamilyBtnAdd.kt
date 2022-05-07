package id.altea.care.view.family.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.mikepenz.fastadapter.binding.BindingViewHolder
import id.altea.care.databinding.ItemAddressBtnAddBinding
import id.altea.care.databinding.ItemFamilyBtnAddBinding

class ItemFamilyBtnAdd(val invoke: () -> Unit
) : AbstractBindingItem<ItemFamilyBtnAddBinding>() {

    override val type: Int
        get() = hashCode()

    override fun createBinding(
            inflater: LayoutInflater,
            parent: ViewGroup?
    ): ItemFamilyBtnAddBinding {
        return ItemFamilyBtnAddBinding.inflate(inflater, parent, false)
    }

    override fun getViewHolder(viewBinding: ItemFamilyBtnAddBinding): BindingViewHolder<ItemFamilyBtnAddBinding> {
        viewBinding.itemFamilyBtnAdd.setOnClickListener { invoke() }
        return super.getViewHolder(viewBinding)
    }
}