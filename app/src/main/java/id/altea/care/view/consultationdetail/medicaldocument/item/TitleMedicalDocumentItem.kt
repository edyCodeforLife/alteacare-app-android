package id.altea.care.view.consultationdetail.medicaldocument.item

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.R
import id.altea.care.databinding.ItemMedicalDocumentTitleBinding

class TitleMedicalDocumentItem(
    val title: String,
    @ColorRes val background: Int = R.color.grayLight2
) : AbstractBindingItem<ItemMedicalDocumentTitleBinding>() {

    override val type: Int
        get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemMedicalDocumentTitleBinding {
        return ItemMedicalDocumentTitleBinding.inflate(inflater, parent, false)
    }

    override fun bindView(binding: ItemMedicalDocumentTitleBinding, payloads: List<Any>) {
        binding.run {
            root.setBackgroundColor(ContextCompat.getColor(root.context, background))
            itemMedicalDocTxtTitle.text = title
        }
    }


}
