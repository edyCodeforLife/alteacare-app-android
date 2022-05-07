package id.altea.care.view.common.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.databinding.ItemGeneralErrorRetryBinding

class ItemGeneralErrorRetry(
    val retryInvoke: () -> Unit
) : AbstractBindingItem<ItemGeneralErrorRetryBinding>() {
    override val type: Int
        get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemGeneralErrorRetryBinding {
        return ItemGeneralErrorRetryBinding.inflate(inflater, parent, false)
    }

    override fun bindView(binding: ItemGeneralErrorRetryBinding, payloads: List<Any>) {
        super.bindView(binding, payloads)
        binding.contentErrorBtnRetry.setOnClickListener { retryInvoke() }
    }
}

