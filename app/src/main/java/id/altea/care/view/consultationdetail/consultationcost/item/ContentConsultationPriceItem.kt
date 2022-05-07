package id.altea.care.view.consultationdetail.consultationcost.item

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.QuoteSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.R
import id.altea.care.core.domain.model.CostConsul
import id.altea.care.databinding.ItemConsultationCostBinding

class ContentConsultationPriceItem(val costConsul : CostConsul) : AbstractBindingItem<ItemConsultationCostBinding>() {
    override val type: Int
        get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemConsultationCostBinding {
        return ItemConsultationCostBinding.inflate(inflater,parent,false)
    }

    @SuppressLint("ResourceAsColor")
    override fun bindView(binding: ItemConsultationCostBinding, payloads: List<Any>) {
        binding.run {
            if (costConsul.label?.contains("voucher",true) == true){
                val voucherLabel = SpannableString("${costConsul.label.substring(0,7)}\n${costConsul.label.substring(8)}")
                voucherLabel.setSpan(ForegroundColorSpan(this.root.context.getColor(R.color.blueDarker)),8,costConsul.label.length,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                itemConsultationCostLabelTxt.text = voucherLabel
                itemConsultationCostPrice.text = costConsul.price
            }else {
                itemConsultationCostLabelTxt.text = costConsul.label
                itemConsultationCostPrice.text = costConsul.price
            }
        }
    }
}