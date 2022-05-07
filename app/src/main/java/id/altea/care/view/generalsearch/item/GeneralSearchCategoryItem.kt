package id.altea.care.view.generalsearch.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.databinding.ItemGeneralSearchCategoryBinding

class GeneralSearchCategoryItem(private val category: String) :
    AbstractBindingItem<ItemGeneralSearchCategoryBinding>() {
    override val type: Int
        get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemGeneralSearchCategoryBinding {
        return ItemGeneralSearchCategoryBinding.inflate(inflater, parent, false)
    }

    override fun bindView(binding: ItemGeneralSearchCategoryBinding, payloads: List<Any>) {
        binding.itemGeneralSearchTxtCategory.text = category
    }
}
