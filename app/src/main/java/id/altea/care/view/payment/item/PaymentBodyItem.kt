package id.altea.care.view.payment.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.core.domain.model.PaymentMethod
import id.altea.care.core.ext.loadImage
import id.altea.care.databinding.ItemPaymentBodyBinding

class PaymentBodyItem(val item: PaymentMethod) : AbstractBindingItem<ItemPaymentBodyBinding>() {
    override val type: Int
        get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemPaymentBodyBinding {
        return ItemPaymentBodyBinding.inflate(inflater, parent, false)
    }

    override fun bindView(binding: ItemPaymentBodyBinding, payloads: List<Any>) {
        binding.run {
            item.let { payment ->
                paymentBodyImgIcon.loadImage(payment.icon.orEmpty())
                paymentBodyTxtPaymentName.text = payment.name
                paymentBodyTxtDesc.text = payment.description
            }
        }
    }
}