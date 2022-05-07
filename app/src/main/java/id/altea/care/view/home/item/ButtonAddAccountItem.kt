package id.altea.care.view.home.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.mikepenz.fastadapter.binding.BindingViewHolder
import id.altea.care.databinding.ItemAccountBtnAddBinding

class ButtonAddAccountItem(
    val invoke: () -> Unit
) : AbstractBindingItem<ItemAccountBtnAddBinding>() {

    override val type: Int
        get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemAccountBtnAddBinding {
        return ItemAccountBtnAddBinding.inflate(inflater, parent, false)
    }

    override fun getViewHolder(viewBinding: ItemAccountBtnAddBinding): BindingViewHolder<ItemAccountBtnAddBinding> {
        viewBinding.itemAccountBtnAdd.setOnClickListener { invoke() }
        return super.getViewHolder(viewBinding)
    }
}
