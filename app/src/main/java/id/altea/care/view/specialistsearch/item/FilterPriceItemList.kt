package id.altea.care.view.specialistsearch.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.chip.Chip
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.mikepenz.fastadapter.binding.BindingViewHolder
import com.mikepenz.fastadapter.listeners.ClickEventHook
import id.altea.care.core.ext.setLayoutParamsHeight
import id.altea.care.databinding.ItemFilterContentBinding
import id.altea.care.databinding.ItemFilterListContentBinding
import id.altea.care.view.specialistsearch.model.SpecialistFilterDayModel
import id.altea.care.view.specialistsearch.model.SpecialistFilterModel
import id.altea.care.view.specialistsearch.model.SpecialistFilterPriceModel

class FilterPriceItemList(
    val callback: (SpecialistFilterModel) -> Unit
) : AbstractBindingItem<ItemFilterListContentBinding>() {

    private val itemAdapter by lazy { ItemAdapter<FilterPriceItem>() }
    private val fastAdapter by lazy { FastAdapter.with(itemAdapter) }

    init {
        fastAdapter.addEventHook(object : ClickEventHook<FilterPriceItem>() {
            override fun onBind(viewHolder: RecyclerView.ViewHolder): View {
                return ((viewHolder as BindingViewHolder<*>).binding as ItemFilterContentBinding).itemFilterContentChipText
            }

            override fun onClick(
                v: View,
                position: Int,
                fastAdapter: FastAdapter<FilterPriceItem>,
                item: FilterPriceItem
            ) {
                for (i in 0 until fastAdapter.itemCount) {
                    if (i != position) {
                        fastAdapter.notifyItemChanged(i, fastAdapter.getItem(i)?.apply {
                            when (data) {
                                is SpecialistFilterPriceModel -> data.isSelected = false
                                is SpecialistFilterDayModel -> data.isSelected = false
                            }
                        })
                    }
                }
                callback(item.data.also {
                    when (it) {
                        is SpecialistFilterPriceModel -> it.isSelected = (v as Chip).isChecked
                        is SpecialistFilterDayModel -> it.isSelected = (v as Chip).isChecked
                    }
                })
            }
        })
    }

    override val type: Int get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemFilterListContentBinding {
        return ItemFilterListContentBinding.inflate(inflater, parent, false)
    }

    fun applyChip(data: List<SpecialistFilterModel>) {
        itemAdapter.clear()
        itemAdapter.add(data.map { FilterPriceItem(it) })
    }

    override fun bindView(
        holder: BindingViewHolder<ItemFilterListContentBinding>,
        payloads: List<Any>
    ) {
        super.bindView(holder, payloads)
        (holder as ViewHolder).loadView()
    }

    override fun getViewHolder(viewBinding: ItemFilterListContentBinding): BindingViewHolder<ItemFilterListContentBinding> {
        return ViewHolder(viewBinding)
    }

    inner class ViewHolder(val viewBinding: ItemFilterListContentBinding) :
        BindingViewHolder<ItemFilterListContentBinding>(viewBinding) {

        init {
            viewBinding.root.setLayoutParamsHeight(0)
        }

        fun loadView() {
            if (itemAdapter.adapterItemCount <= 0) return

            viewBinding.root.setLayoutParamsHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
            viewBinding.itemFilterContentRecycler.run {
                val flex = FlexboxLayoutManager(itemView.context)
                flex.flexDirection = FlexDirection.ROW
                flex.justifyContent = JustifyContent.FLEX_START
                layoutManager = flex

                isNestedScrollingEnabled = false
                adapter = fastAdapter
            }
        }
    }
}
