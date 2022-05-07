package id.altea.care.view.consultation.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.core.domain.model.ConsultationFee
import id.altea.care.core.ext.toRupiahFormat
import id.altea.care.databinding.ItemPaymentFeeBinding

class PaymentFeeItem(val fee: ConsultationFee) : AbstractBindingItem<ItemPaymentFeeBinding>() {
    override val type: Int
        get() = hashCode()

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?): ItemPaymentFeeBinding {
        return ItemPaymentFeeBinding.inflate(inflater, parent, false)
    }

    override fun bindView(binding: ItemPaymentFeeBinding, payloads: List<Any>) {
        binding.run {
            paymentItemTxtLabel.text = fee.label
            paymentItemTxtPrice.text = (fee.amount ?: 0L).toRupiahFormat()
        }
    }
}