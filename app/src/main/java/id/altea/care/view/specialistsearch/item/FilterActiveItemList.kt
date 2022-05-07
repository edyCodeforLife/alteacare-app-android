package id.altea.care.view.specialistsearch.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.mikepenz.fastadapter.binding.BindingViewHolder
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.fastadapter.select.getSelectExtension
import id.altea.care.core.ext.setLayoutParamsHeight
import id.altea.care.databinding.ItemFilterActiveListBinding
import id.altea.care.view.specialistsearch.model.FilterActiveModel

class FilterActiveItemList(
    val callback: (FilterActiveModel) -> Unit
) : AbstractBindingItem<ItemFilterActiveListBinding>() {

    private val itemAdapter by lazy { ItemAdapter<IItem<*>>() }
    private val fastAdapter by lazy { FastAdapter.with(itemAdapter) }
    private var selectExtension = fastAdapter.getSelectExtension()

    init {
        selectExtension.isSelectable = true
        fastAdapter.addEventHook(object : ClickEventHook<FilterActiveItem>() {
            override fun onClick(
                v: View,
                position: Int,
                fastAdapter: FastAdapter<FilterActiveItem>,
                item: FilterActiveItem
            ) {
                callback(item.filterActive)
            }

            override fun onBind(viewHolder: RecyclerView.ViewHolder): View {
                return viewHolder.itemView
            }
        })
    }

    override val type: Int get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemFilterActiveListBinding {
        return ItemFilterActiveListBinding.inflate(inflater, parent, false)
    }

    fun applyChip(data: List<FilterActiveModel>) {
        itemAdapter.clear()
        itemAdapter.add(data.filter { it.view.isNotBlank() }.map { item -> FilterActiveItem(item) })
        fastAdapter.notifyAdapterDataSetChanged()
    }

    override fun bindView(
        holder: BindingViewHolder<ItemFilterActiveListBinding>,
        payloads: List<Any>
    ) {
        super.bindView(holder, payloads)
        (holder as ViewHolder).loadView()
    }

    override fun getViewHolder(viewBinding: ItemFilterActiveListBinding): BindingViewHolder<ItemFilterActiveListBinding> {
        return ViewHolder(viewBinding)
    }

    inner class ViewHolder(val viewBinding: ItemFilterActiveListBinding) :
        BindingViewHolder<ItemFilterActiveListBinding>(viewBinding) {

        init {
            viewBinding.root.setLayoutParamsHeight(0)
        }

        fun loadView() {
            viewBinding.itemListBtnDelete.isVisible = itemAdapter.adapterItemCount > 0
            if (itemAdapter.adapterItemCount <= 0) return


            viewBinding.root.setLayoutParamsHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
            viewBinding.itemDaysListRecycler.run {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = fastAdapter
            }
        }
    }
}
