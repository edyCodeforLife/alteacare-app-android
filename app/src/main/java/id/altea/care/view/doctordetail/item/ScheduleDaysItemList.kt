package id.altea.care.view.doctordetail.item

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
import id.altea.care.databinding.ItemScheduleDaysListBinding
import id.altea.care.view.doctordetail.item.model.ScheduleDayItemModel
import id.altea.care.view.specialistsearch.model.FilterActiveModel

class ScheduleDaysItemList(
    val callback: (ScheduleDayItemModel) -> Unit
) : AbstractBindingItem<ItemScheduleDaysListBinding>() {

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
    ): ItemScheduleDaysListBinding {
        return ItemScheduleDaysListBinding.inflate(inflater, parent, false)
    }

    fun applyChip(data: List<ScheduleDayItemModel>, filterActive: FilterActiveModel.FilterDate?) {
        itemAdapter.clear()
        itemAdapter.add(data.mapIndexed { index, scheduleDayItemModel ->
            ScheduleDaysItem(scheduleDayItemModel).apply {
                this.isSelected = when {
                    filterActive == null && index == 0 -> true
                    else -> filterActive?.date == scheduleDayItemModel.dateParam
                }
            }
        })
        fastAdapter.notifyAdapterDataSetChanged()
    }

    override fun bindView(
        holder: BindingViewHolder<ItemScheduleDaysListBinding>,
        payloads: List<Any>
    ) {
        super.bindView(holder, payloads)
        (holder as ViewHolder).loadView()
    }

    override fun getViewHolder(viewBinding: ItemScheduleDaysListBinding): BindingViewHolder<ItemScheduleDaysListBinding> {
        return ViewHolder(viewBinding)
    }

    inner class ViewHolder(val viewBinding: ItemScheduleDaysListBinding) :
        BindingViewHolder<ItemScheduleDaysListBinding>(viewBinding) {

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
