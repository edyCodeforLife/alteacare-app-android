package id.altea.care.view.specialistsearch.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.databinding.ItemEmptyDoctorSpecialistBinding

class UnavailableDoctorItem : AbstractBindingItem<ItemEmptyDoctorSpecialistBinding>() {

    override val type: Int
        get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemEmptyDoctorSpecialistBinding {
        return ItemEmptyDoctorSpecialistBinding.inflate(inflater, parent, false)
    }

}
