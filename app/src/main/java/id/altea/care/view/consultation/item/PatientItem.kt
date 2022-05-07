package id.altea.care.view.consultation.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.core.domain.model.Patient
import id.altea.care.databinding.ItemPatientBinding

class PatientItem(val patient: Patient) : AbstractBindingItem<ItemPatientBinding>() {
    override val type: Int
        get() = hashCode()

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?): ItemPatientBinding {
        return ItemPatientBinding.inflate(inflater, parent, false)
    }

    override fun bindView(binding: ItemPatientBinding, payloads: List<Any>) {

        binding.run {
            itemPatientLeft.text = patient.left
            itemPatientRight.text = patient.right
        }

    }
}