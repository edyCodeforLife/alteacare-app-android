package id.altea.care.view.common.item

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.StringRes
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.R
import id.altea.care.databinding.ItemEmptyAppointmentBinding

class AppointmentEmptyItem(val message: Int = R.string.empty_appointment) :
    AbstractBindingItem<ItemEmptyAppointmentBinding>() {
    override val type: Int
        get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemEmptyAppointmentBinding {
        return ItemEmptyAppointmentBinding.inflate(inflater, parent, false)
    }

    override fun bindView(binding: ItemEmptyAppointmentBinding, payloads: List<Any>) {
        super.bindView(binding, payloads)
        binding.emptyScheduleText.text = binding.root.context.getString(message)
    }
}
