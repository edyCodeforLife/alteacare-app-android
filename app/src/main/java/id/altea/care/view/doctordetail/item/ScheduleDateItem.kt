package id.altea.care.view.doctordetail.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.databinding.ItemScheduleDateBinding

class ScheduleDateItem(
    val sortType: String
) : AbstractBindingItem<ItemScheduleDateBinding>() {

    override val type: Int get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemScheduleDateBinding {
        return ItemScheduleDateBinding.inflate(inflater, parent, false)
    }

    override fun bindView(binding: ItemScheduleDateBinding, payloads: List<Any>) {
        binding.itemScheduleDateTxtDate.text = sortType
    }
}
