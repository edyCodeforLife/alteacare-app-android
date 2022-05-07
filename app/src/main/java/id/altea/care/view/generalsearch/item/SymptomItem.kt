package id.altea.care.view.generalsearch.item

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.R
import id.altea.care.core.domain.model.Symptoms
import id.altea.care.databinding.ItemGeneralSearchContentBinding

class SymptomItem(
    val data: Symptoms
) : AbstractBindingItem<ItemGeneralSearchContentBinding>() {

    override val type: Int get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemGeneralSearchContentBinding {
        return ItemGeneralSearchContentBinding.inflate(inflater, parent, false)
    }

    override fun bindView(binding: ItemGeneralSearchContentBinding, payloads: List<Any>) {
        super.bindView(binding, payloads)
        binding.run {
            itemGeneralSearchTxtContent.text = data.name
            itemGeneralSearchTxtContent.setTextColor(Color.BLACK)
            itemGeneralSearchTxtContent.setTextAppearance(R.style.TextInter700)
        }
    }
}