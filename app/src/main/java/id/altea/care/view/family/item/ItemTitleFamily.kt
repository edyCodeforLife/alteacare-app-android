package id.altea.care.view.family.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.databinding.ItemTitleFamilyMemberBinding

class ItemTitleFamily(val title : String) : AbstractBindingItem<ItemTitleFamilyMemberBinding>() {
    override val type: Int
        get() = hashCode()

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?): ItemTitleFamilyMemberBinding {
        return ItemTitleFamilyMemberBinding.inflate(inflater, parent, false)
    }

    override fun bindView(binding: ItemTitleFamilyMemberBinding, payloads: List<Any>) {
        binding.run {
            itemTitleFamilyMember.text = title
        }
    }
}