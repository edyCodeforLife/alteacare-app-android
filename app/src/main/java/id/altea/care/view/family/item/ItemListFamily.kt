package id.altea.care.view.family.item

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.R
import id.altea.care.core.domain.model.PatientFamily
import id.altea.care.core.domain.model.UserAddress
import id.altea.care.core.ext.getCompleteAddress
import id.altea.care.core.ext.loadImage
import id.altea.care.databinding.ItemAddressBinding
import id.altea.care.databinding.ItemListFamilyBinding
import id.altea.care.view.consultation.item.PatientItem

class ItemListFamily(val patient : PatientFamily) : AbstractBindingItem<ItemListFamilyBinding>() {
    override val type: Int
        get() = hashCode()

    fun isPrimary(): Boolean {
        return patient.isDefault!!
    }

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?): ItemListFamilyBinding {
        return ItemListFamilyBinding.inflate(inflater, parent, false)
    }

    override fun bindView(binding: ItemListFamilyBinding, payloads: List<Any>) {
        binding.run {
            itemFamilyImgMore.isVisible = !isPrimary()
            itemListImg.loadImage(patient.cardPhoto.orEmpty(), R.drawable.ic_change_photo_profile)
            itemListTextName.text = "${patient.firstName} ${patient.lastName}"
            itemListContactFamily.text  = patient.familyRelationType?.name
        }
    }
}
