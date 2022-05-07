package id.altea.care.view.consultationdetail.medicaldocument.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.databinding.ItemMedicalDocumentEmptyBinding

class EmptyMedicalDocumentItem : AbstractBindingItem<ItemMedicalDocumentEmptyBinding>() {

    override val type: Int
        get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemMedicalDocumentEmptyBinding {
        return ItemMedicalDocumentEmptyBinding.inflate(inflater, parent, false)
    }
}
