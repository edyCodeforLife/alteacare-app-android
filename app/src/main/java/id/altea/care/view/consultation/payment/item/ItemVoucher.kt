package id.altea.care.view.consultation.payment.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.mikepenz.fastadapter.listeners.ClickEventHook
import id.altea.care.core.domain.model.Voucher
import id.altea.care.core.ext.asBinding
import id.altea.care.databinding.ItemVoucherBinding

class ItemVoucher(val voucher : Voucher) : AbstractBindingItem<ItemVoucherBinding>() {

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?): ItemVoucherBinding {
     return ItemVoucherBinding.inflate(inflater,parent,false)
    }

    override val type: Int
        get() = 0

    override fun bindView(binding: ItemVoucherBinding, payloads: List<Any>) {
        binding.run {
            itemVoucherTextLabel.text = voucher.voucherCode
            itemVoucherTextNominal.text = voucher.discount
        }
    }

    class ItemVoucherSelectEvent(val invoke : (Int) -> Unit) :
        ClickEventHook<ItemVoucher>() {
        override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
            return viewHolder.asBinding<ItemVoucherBinding> { it.itemVoucherTextRemove }
        }

        override fun onClick(
            v: View,
            position: Int,
            fastAdapter: FastAdapter<ItemVoucher>,
            item: ItemVoucher
        ) {
            invoke(position)
        }
    }
}