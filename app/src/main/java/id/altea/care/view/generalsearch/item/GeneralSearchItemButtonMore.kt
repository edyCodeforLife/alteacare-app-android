package id.altea.care.view.generalsearch.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.core.domain.model.DataGeneralSearchButtonMore
import id.altea.care.databinding.ItemButtonGeneralSearchBinding

class GeneralSearchItemButtonMore(
    val dataGeneralSearchButtonMore: DataGeneralSearchButtonMore
) : AbstractBindingItem<ItemButtonGeneralSearchBinding>() {

    override val type: Int get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemButtonGeneralSearchBinding {
        return ItemButtonGeneralSearchBinding.inflate(inflater, parent, false)
    }

    override fun bindView(binding: ItemButtonGeneralSearchBinding, payloads: List<Any>) {
        binding.run {
            btnShowMore.text = dataGeneralSearchButtonMore.textButton
        }
    }
}