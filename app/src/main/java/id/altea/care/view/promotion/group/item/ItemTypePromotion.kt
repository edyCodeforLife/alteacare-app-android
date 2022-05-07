package id.altea.care.view.promotion.group.item

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginTop
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.mikepenz.fastadapter.listeners.ClickEventHook
import id.altea.care.R
import id.altea.care.core.domain.model.ItemPromotionListGroup
import id.altea.care.databinding.ItemPromotionBinding
import kotlinx.android.synthetic.main.item_promotion_banner.view.*
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout


class ItemTypePromotion(
    val index : Int,
    val title: String,
    val promotionTypeId: String,
    val itemPromotionListGroup: List<ItemPromotionListGroup?>?
) : AbstractBindingItem<ItemPromotionBinding>() {

    val itemAdapter = ItemAdapter<ItemImagePromotion>()
    val fastAdapter = FastAdapter.with(itemAdapter)
    var eventTextListener: onClickListener? = null

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemPromotionBinding {
        return ItemPromotionBinding.inflate(inflater, parent, false)
    }

    override val type: Int
        get() = hashCode()

    override fun bindView(binding: ItemPromotionBinding, payloads: List<Any>) {
        binding.run {
            if (index == 0){
                firstLayoutParam(txtPromo,txtShowMorePromoGroup)
            }
            txtPromo.text = title
            itemPromotionListGroup?.forEachIndexed { index, itemPromotionListGroup ->
                if (title == itemPromotionListGroup?.promotionType) {
                    itemAdapter.add(ItemImagePromotion(itemPromotionListGroup))
                }
            }
            recyclerItemPromotion.apply {
                PagerSnapHelper().attachToRecyclerView(this)
                setPadding(0, 0, 50, 0)
                clipToPadding = false
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = fastAdapter
            }

        }
    }

    private fun firstLayoutParam(txtPromo : TextView,txtShowMorePromoGroup : TextView) {
        val layoutParams = (txtPromo.layoutParams as? ViewGroup.MarginLayoutParams)
        val layoutParamsShowMore = (txtShowMorePromoGroup.layoutParams as? ViewGroup.MarginLayoutParams)
        layoutParams?.setMargins(0, 20, 0, 0)
        layoutParamsShowMore?.setMargins(0,20,0,0)
        txtPromo.layoutParams = layoutParams
        txtShowMorePromoGroup.layoutParams  = layoutParamsShowMore
    }

    init {
        fastAdapter.addEventHook(object : ClickEventHook<ItemImagePromotion>() {

            override fun onBindMany(viewHolder: RecyclerView.ViewHolder): List<View>? {
                return listOf(
                    viewHolder.itemView.itemBannerImgPromotion
                )
            }

            override fun onClick(
                v: View,
                position: Int,
                fastAdapter: FastAdapter<ItemImagePromotion>,
                item: ItemImagePromotion
            ) {
                when (v.id) {
                    R.id.itemBannerImgPromotion ->{
                        eventTextListener?.textChangeListener(item.item?.id ?: 0)
                    }
                }
            }

        })
    }

    fun setListenerText(onTextChangeListener: onClickListener) {
        this.eventTextListener = onTextChangeListener
    }

    interface onClickListener {
        fun textChangeListener(statusClikced: Int)
    }
}