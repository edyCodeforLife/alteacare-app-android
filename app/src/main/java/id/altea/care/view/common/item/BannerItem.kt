package id.altea.care.view.common.item

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.mikepenz.fastadapter.binding.BindingViewHolder
import id.altea.care.R
import id.altea.care.core.domain.model.Banner
import id.altea.care.core.ext.loadImage
import id.altea.care.databinding.ItemBannerBinding
import id.altea.care.databinding.ItemSpecialistBinding
import id.altea.care.view.specialist.model.SpecialistItemModel

class BannerItem(val itemImgBanner : Banner?) :
    AbstractBindingItem<ItemBannerBinding>() {
    override val type: Int
        get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemBannerBinding {
        return ItemBannerBinding.inflate(inflater, parent, false)
    }

    override fun getViewHolder(parent: ViewGroup): BindingViewHolder<ItemBannerBinding> {
        val holder = super.getViewHolder(parent)
        holder.setIsRecyclable(false)
        return holder
    }

    override fun bindView(binding: ItemBannerBinding, payloads: List<Any>) {
        binding.itemBannerImg.loadImage(
            itemImgBanner?.imageMobile.orEmpty(),
            placeholder = R.drawable.ic_logo_loading
        )
    }
}