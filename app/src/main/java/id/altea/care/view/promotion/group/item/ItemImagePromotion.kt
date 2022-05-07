package id.altea.care.view.promotion.group.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.R
import id.altea.care.core.domain.model.ItemPromotionListGroup
import id.altea.care.core.ext.loadImage
import id.altea.care.databinding.ItemPromotionBannerBinding

class ItemImagePromotion(val item : ItemPromotionListGroup?) : AbstractBindingItem<ItemPromotionBannerBinding>() {
    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemPromotionBannerBinding {
        return ItemPromotionBannerBinding.inflate(inflater,parent,false)
    }

    override val type: Int
        get() = hashCode()

    override fun bindView(binding: ItemPromotionBannerBinding, payloads: List<Any>) {
        binding.run {
            itemBannerImgPromotion.loadImage(
                item?.image.orEmpty(),
                placeholder = R.drawable.ic_logo_loading
            )
        }
    }
}