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
import id.altea.care.view.specialistsearch.model.SpecialistFilterHospitalModel
import id.altea.care.view.specialistsearch.model.SpecialistFilterModel
import id.altea.care.view.specialistsearch.model.SpecialistFilterSpecializationModel

class FilterContentItemList(
    val callback: (SpecialistFilterModel) -> Unit
) : AbstractBindingItem<ItemFilterListContentBinding>() {

    private val itemAdapter by lazy { ItemAdapter<FilterContentItem>() }
    private val fastAdapter by lazy { FastAdapter.with(itemAdapter) }

    init {
        fastAdapter.addEventHook(object : ClickEventHook<FilterContentItem>() {
            override fun onBind(viewHolder: RecyclerView.ViewHolder): View {
                return ((viewHolder as BindingViewHolder<*>).binding as ItemFilterContentBinding).itemFilterContentChipText
            }

            override fun onClick(
                v: View,
                position: Int,
                fastAdapter: FastAdapter<FilterContentItem>,
                item: FilterContentItem
            ) {
                when (item.data) {
                    is SpecialistFilterSpecializationModel -> {
                        callback(item.data.apply {
                            isSelected = (v as Chip).isChecked
                        })
                    }
                    is SpecialistFilterHospitalModel -> {
                        callback(item.data.apply {
                            isSelected = (v as Chip).isChecked
                        })
                    }
                }
            }
        })
    }

    override val type: Int get() = hashCode()

    fun getTypeFilter() : SpecialistFilterModel? {
        if (itemAdapter.adapterItems.isNotEmpty()) {
            return itemAdapter.adapterItems[0].data
        }
        return null
    }


    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemFilterListContentBinding {
        return ItemFilterListContentBinding.inflate(inflater, parent, false)
    }

    fun applyChip(data: List<SpecialistFilterModel>) {
        itemAdapter.clear()
        itemAdapter.add(data.map { FilterContentItem(it) })
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
