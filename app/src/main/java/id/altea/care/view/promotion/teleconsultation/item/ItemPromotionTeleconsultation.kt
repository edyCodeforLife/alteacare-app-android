package id.altea.care.view.promotion.teleconsultation.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.R
import id.altea.care.core.domain.model.PromotionList
import id.altea.care.core.ext.loadImage
import id.altea.care.databinding.ItemPromotionDetailListBinding

class ItemPromotionTeleconsultation(val itemPromotion : PromotionList) : AbstractBindingItem<ItemPromotionDetailListBinding>() {
    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemPromotionDetailListBinding {
        return  ItemPromotionDetailListBinding.inflate(inflater,parent,false)
    }

    override val type: Int
        get() = hashCode()


    override fun bindView(binding: ItemPromotionDetailListBinding, payloads: List<Any>) {
        binding.run {
            itemBannerImgPromotionDetail.loadImage(
                itemPromotion.image,
                placeholder = R.drawable.ic_logo_loading
            )

            promotionListDetailText.text = itemPromotion.title

        }
    }
}