package id.altea.care.view.common.item

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.mikepenz.fastadapter.binding.BindingViewHolder
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.fastadapter.select.SelectExtension
import com.mikepenz.fastadapter.select.getSelectExtension
import id.altea.care.R
import id.altea.care.core.ext.asBinding
import id.altea.care.databinding.ItemAddressBinding
import id.altea.care.databinding.ItemSingleSimpleCancelBinding
import id.altea.care.databinding.ItemSingleSimpleSelectedBinding
import id.altea.care.view.address.addressform.item.ItemAddressSingleSelectable
import kotlinx.android.synthetic.main.item_single_simple_cancel.view.*
import kotlinx.coroutines.delay

class CancelItem(private val optionCancel : String) : AbstractBindingItem<ItemSingleSimpleCancelBinding>() {

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemSingleSimpleCancelBinding {
        return ItemSingleSimpleCancelBinding.inflate(inflater,parent,false)
    }

    override val type: Int
        get() = hashCode()

    override fun bindView(binding: ItemSingleSimpleCancelBinding, payloads: List<Any>) {
      binding.itemSampleRadioButton.text = optionCancel
      binding.itemSampleRadioButton.isChecked = isSelected
    }


    class CancelClickEvent(val invoke : (String) -> Unit) : ClickEventHook<CancelItem>(){

        override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
            return viewHolder.asBinding<ItemSingleSimpleCancelBinding> { it.itemSampleRadioButton }
        }
        override fun onClick(
            v: View,
            position: Int,
            fastAdapter: FastAdapter<CancelItem>,
            item: CancelItem
        ) {

            val selectExtension = fastAdapter.getSelectExtension()
            val selections = selectExtension.selections
            if (selections.isNotEmpty()) {
                val selectedPosition = selections.iterator().next()
                selectExtension.deselect()
                fastAdapter.notifyItemChanged(selectedPosition)
            }
            selectExtension.select(position)
            invoke(item.optionCancel)
        }

    }
}