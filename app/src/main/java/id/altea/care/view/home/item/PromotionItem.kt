package id.altea.care.view.home.item

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.mikepenz.fastadapter.binding.BindingViewHolder
import id.altea.care.R
import id.altea.care.core.domain.model.PromotionList
import id.altea.care.core.ext.loadImage
import id.altea.care.databinding.ItemBannerBinding
import id.altea.care.databinding.ItemPromotionBannerBinding

class PromotionItem(val item: PromotionList) : AbstractBindingItem<ItemPromotionBannerBinding>() {
        override val type: Int
        get() = hashCode()

        override fun createBinding(
            inflater: LayoutInflater,
            parent: ViewGroup?
        ): ItemPromotionBannerBinding {
            return ItemPromotionBannerBinding.inflate(inflater, parent, false)
        }


        override fun getViewHolder(parent: ViewGroup): BindingViewHolder<ItemPromotionBannerBinding> {
            val holder = super.getViewHolder(parent)
            holder.setIsRecyclable(false)
            return holder
        }

        override fun bindView(binding: ItemPromotionBannerBinding, payloads: List<Any>) {

            binding.itemBannerImgPromotion.loadImage(
                item.image,
                placeholder = R.drawable.ic_logo_loading
            )

        }



}