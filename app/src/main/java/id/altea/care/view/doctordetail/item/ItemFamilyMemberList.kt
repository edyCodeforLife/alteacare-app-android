package id.altea.care.view.doctordetail.item

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.R
import id.altea.care.core.domain.model.PatientFamily
import id.altea.care.core.ext.loadImage
import id.altea.care.databinding.ItemFamilyMemberListBinding
import id.altea.care.databinding.ItemListFamilyBinding

class ItemFamilyMemberList(val patient : PatientFamily) : AbstractBindingItem<ItemFamilyMemberListBinding>() {

    override val type: Int
        get() = hashCode()

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?): ItemFamilyMemberListBinding {
        return ItemFamilyMemberListBinding.inflate(inflater, parent, false)
    }

    @SuppressLint("SetTextI18n")
    override fun bindView(binding: ItemFamilyMemberListBinding, payloads: List<Any>) {
        binding.run {
            itemListImg.loadImage(patient.cardPhoto.orEmpty(), R.drawable.ic_change_photo_profile)
            itemListTextName.text = "${patient.firstName} ${patient.lastName}"
            itemListContactFamily.text  = patient.familyRelationType?.name
        }
    }
}
