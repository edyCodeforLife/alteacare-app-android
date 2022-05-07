package id.altea.care.view.common.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.databinding.ItemErrorRetryBinding

class ErrorRetryItem(
    val retryInvoke: () -> Unit
) : AbstractBindingItem<ItemErrorRetryBinding>() {
    override val type: Int
        get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemErrorRetryBinding {
        return ItemErrorRetryBinding.inflate(inflater, parent, false)
    }

    override fun bindView(binding: ItemErrorRetryBinding, payloads: List<Any>) {
        super.bindView(binding, payloads)
        binding.itemErrorBtnRetry.setOnClickListener { retryInvoke() }
    }
}

