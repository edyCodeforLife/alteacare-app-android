package id.altea.care.view.generalsearch.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.databinding.ItemGeneralSearchContentBinding
import id.altea.care.view.generalsearch.model.SearchContent

class GeneralSearchContentItem(val searchContent: SearchContent) :
    AbstractBindingItem<ItemGeneralSearchContentBinding>() {

    override val type: Int
        get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemGeneralSearchContentBinding {
        return ItemGeneralSearchContentBinding.inflate(inflater, parent, false)
    }

    override fun bindView(binding: ItemGeneralSearchContentBinding, payloads: List<Any>) {
        binding.itemGeneralSearchTxtContent.text = searchContent.content
    }
}

enum class TypeContent {
    SPECIALIST, SYMTOM
}
