package id.altea.care.view.specialistsearch.item

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.databinding.ItemFilterTitleBinding
import id.altea.care.view.specialistsearch.model.FilterTitle

class FilterTitleItem(
    val filterTitle: FilterTitle
) : AbstractBindingItem<ItemFilterTitleBinding>() {

    override val type: Int get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemFilterTitleBinding {
        return ItemFilterTitleBinding.inflate(inflater, parent, false)
    }

    override fun bindView(binding: ItemFilterTitleBinding, payloads: List<Any>) {
        binding.run {
            itemFilterTxtTitle.text = filterTitle.title
            itemFilterTxtShowAll.isVisible = filterTitle.visibleShowAll
        }
    }
}