package id.altea.care.view.common.item

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.databinding.ItemProgressbarBinding

class ProgressBarItem : AbstractBindingItem<ItemProgressbarBinding>() {
    override val type: Int
        get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemProgressbarBinding {
        return ItemProgressbarBinding.inflate(inflater, parent, false)
    }
}
