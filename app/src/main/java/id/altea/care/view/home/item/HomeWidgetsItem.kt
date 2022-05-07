package id.altea.care.view.home.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.core.domain.model.Widgets
import id.altea.care.core.ext.loadImage
import id.altea.care.databinding.ItemMenuHomeBinding

class HomeWidgetsItem(val itemWidgetsHome: Widgets) : AbstractBindingItem<ItemMenuHomeBinding>() {

    override val type: Int
        get() = hashCode()

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?): ItemMenuHomeBinding {
        return ItemMenuHomeBinding.inflate(inflater, parent, false)
    }

    override fun bindView(binding: ItemMenuHomeBinding, payloads: List<Any>) {
        super.bindView(binding, payloads)
        binding.run {
            imgItemMenuHome.loadImage(itemWidgetsHome.icon)
            txtItemMenuHome.text = itemWidgetsHome.title
        }
    }
}