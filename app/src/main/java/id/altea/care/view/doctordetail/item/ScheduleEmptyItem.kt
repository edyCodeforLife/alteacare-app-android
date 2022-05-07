package id.altea.care.view.doctordetail.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.databinding.ItemEmptyScheduleBinding

class ScheduleEmptyItem :
    AbstractBindingItem<ItemEmptyScheduleBinding>() {
    override val type: Int
        get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemEmptyScheduleBinding {
        return ItemEmptyScheduleBinding.inflate(inflater, parent, false)
    }
}
