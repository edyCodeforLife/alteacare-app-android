package id.altea.care.view.common.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.databinding.ItemLoadingLottieBinding

class ItemLoadingLottie : AbstractBindingItem<ItemLoadingLottieBinding>() {
    override val type: Int
        get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemLoadingLottieBinding {
        return ItemLoadingLottieBinding.inflate(inflater, parent, false)
    }
}
