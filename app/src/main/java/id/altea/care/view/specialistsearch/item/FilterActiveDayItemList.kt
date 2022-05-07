package id.altea.care.view.specialistsearch.item

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.mikepenz.fastadapter.binding.BindingViewHolder
import com.mikepenz.fastadapter.select.getSelectExtension
import id.altea.care.core.ext.setLayoutParamsHeight
import id.altea.care.databinding.ItemFilterDaysListBinding
import id.altea.care.view.specialistsearch.model.SpecialistFilterDayModel

class FilterActiveDayItemList(
    val callback: (SpecialistFilterDayModel) -> Unit
) : AbstractBindingItem<ItemFilterDaysListBinding>() {

    private val itemAdapter by lazy { ItemAdapter<IItem<*>>() }
    private val fastAdapter by lazy { FastAdapter.with(itemAdapter) }
    private var selectExtension = fastAdapter.getSelectExtension()

    init {
        selectExtension.isSelectable = true
        fastAdapter.addEventHook(ScheduleDayClickEvent { callback(it) })
    }

    override val type: Int get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemFilterDaysListBinding {
        return ItemFilterDaysListBinding.inflate(inflater, parent, false)
    }

    fun applyChip(data: List<SpecialistFilterDayModel>) {
        itemAdapter.clear()
        itemAdapter.add(data.map { item ->
            FilterActiveDayItem(item).apply { isSelected = item.isSelected }
        })
        fastAdapter.notifyAdapterDataSetChanged()
    }

    override fun bindView(
        holder: BindingViewHolder<ItemFilterDaysListBinding>,
        payloads: List<Any>
    ) {
        super.bindView(holder, payloads)
        (holder as ViewHolder).loadView()
    }

    override fun getViewHolder(viewBinding: ItemFilterDaysListBinding): BindingViewHolder<ItemFilterDaysListBinding> {
        return ViewHolder(viewBinding)
    }

    inner class ViewHolder(val viewBinding: ItemFilterDaysListBinding) :
        BindingViewHolder<ItemFilterDaysListBinding>(viewBinding) {

        init {
            viewBinding.root.setLayoutParamsHeight(0)
        }

        fun loadView() {
            if (itemAdapter.adapterItemCount <= 0) return

            viewBinding.root.setLayoutParamsHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
            viewBinding.itemDaysListRecycler.run {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = fastAdapter
            }
        }
    }
}
