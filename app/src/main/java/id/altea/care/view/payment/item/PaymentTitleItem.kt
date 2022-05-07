package id.altea.care.view.payment.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.databinding.ItemPatientBinding
import id.altea.care.databinding.ItemPaymentTitleBinding

class PaymentTitleItem (val title : String) : AbstractBindingItem<ItemPaymentTitleBinding>(){
    override val type: Int
        get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemPaymentTitleBinding {
        return ItemPaymentTitleBinding.inflate(inflater,parent,false)
    }

    override fun bindView(binding: ItemPaymentTitleBinding, payloads: List<Any>) {

        binding.run {
            paymentTextMethod.text = title
        }

    }

}