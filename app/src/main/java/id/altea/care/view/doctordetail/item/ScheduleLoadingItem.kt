package id.altea.care.view.doctordetail.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.databinding.ItemScheduleLoadingBinding

class ScheduleLoadingItem : AbstractBindingItem<ItemScheduleLoadingBinding>() {

    override val type: Int get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemScheduleLoadingBinding {
        return ItemScheduleLoadingBinding.inflate(inflater, parent, false)
    }

    override fun bindView(binding: ItemScheduleLoadingBinding, payloads: List<Any>) {}
}
