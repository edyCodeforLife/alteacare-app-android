package id.altea.care.view.doctordetail.item

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.mikepenz.fastadapter.binding.BindingViewHolder
import com.mikepenz.fastadapter.select.getSelectExtension
import id.altea.care.core.domain.model.DoctorSchedule
import id.altea.care.core.ext.setLayoutParamsHeight
import id.altea.care.databinding.ItemScheduleTimeListBinding

class ScheduleTimeItemList(
        val callback: (DoctorSchedule) -> Unit
) : AbstractBindingItem<ItemScheduleTimeListBinding>() {

    private val itemAdapter = ItemAdapter<ScheduleTimeItem>()
    private val fastAdapter = FastAdapter.with(itemAdapter)
    private var selectExtension = fastAdapter.getSelectExtension()

    init {
        selectExtension.isSelectable = true
        fastAdapter.addEventHook(ScheduleTimeClickEvent { callback(it) })
    }

    override val type: Int get() = hashCode()

    override fun createBinding(
            inflater: LayoutInflater,
            parent: ViewGroup?
    ): ItemScheduleTimeListBinding {
        return ItemScheduleTimeListBinding.inflate(inflater, parent, false)
    }

    fun applyChip(data: List<DoctorSchedule>) {
        itemAdapter.clear()
        itemAdapter.add(data.map { ScheduleTimeItem(it) })
        fastAdapter.notifyAdapterDataSetChanged()
    }

    override fun bindView(
            holder: BindingViewHolder<ItemScheduleTimeListBinding>,
            payloads: List<Any>
    ) {
        super.bindView(holder, payloads)
        (holder as ViewHolder).loadView()
    }

    override fun getViewHolder(viewBinding: ItemScheduleTimeListBinding): BindingViewHolder<ItemScheduleTimeListBinding> {
        return ViewHolder(viewBinding)
    }

    inner class ViewHolder(val viewBinding: ItemScheduleTimeListBinding) :
            BindingViewHolder<ItemScheduleTimeListBinding>(viewBinding) {

        init {
            viewBinding.root.setLayoutParamsHeight(0)
        }

        fun loadView() {
            if (itemAdapter.adapterItemCount <= 0) return

            viewBinding.root.setLayoutParamsHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
            viewBinding.itemTimeListRecycler.run {
                layoutManager = object : GridLayoutManager(context, 3, VERTICAL, false) {
                    override fun checkLayoutParams(lp: RecyclerView.LayoutParams): Boolean {
                        lp.width = width / spanCount
                        return true
                    }
                }
                adapter = fastAdapter
            }
        }
    }
}