package id.altea.care.view.generalsearch.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.core.domain.model.Doctor
import id.altea.care.core.ext.loadImage
import id.altea.care.databinding.ItemGeneralSearchDoctorBinding

class GeneralSearchDoctorItem(val doctor: Doctor) :
        AbstractBindingItem<ItemGeneralSearchDoctorBinding>() {

    override val type: Int
        get() = hashCode()

    override fun createBinding(
            inflater: LayoutInflater,
            parent: ViewGroup?
    ): ItemGeneralSearchDoctorBinding {
        return ItemGeneralSearchDoctorBinding.inflate(inflater, parent, false)
    }

    override fun bindView(binding: ItemGeneralSearchDoctorBinding, payloads: List<Any>) {
        binding.run {
            doctor.photo?.let { itemDoctorSpImgDoctor.loadImage(it) }
            itemDoctorSpTxtDoctorName.text = doctor.doctorName
            itemDoctorSpTxtExperience.text = doctor.experience
            itemDoctorSpTxtSpecialistName.text = doctor.specialization?.name
        }
    }
}
