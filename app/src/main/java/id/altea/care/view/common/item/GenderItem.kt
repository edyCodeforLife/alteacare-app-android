package id.altea.care.view.common.item

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.mikepenz.fastadapter.listeners.ClickEventHook
import id.altea.care.databinding.ItemSingleSimpleSelectedBinding
import kotlinx.android.synthetic.main.item_single_simple_selected.view.*

/**
 * Created by trileksono on 04/03/21.
 */
class GenderItem(private val gender: String) :
    AbstractBindingItem<ItemSingleSimpleSelectedBinding>() {

    override val type: Int get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemSingleSimpleSelectedBinding {
        return ItemSingleSimpleSelectedBinding.inflate(inflater, parent, false)
    }


    override fun bindView(binding: ItemSingleSimpleSelectedBinding, payloads: List<Any>) {
        binding.itemSampleRadioButton.text = gender
    }

    class GenderClickEvent(val invoke: (String) -> Unit) : ClickEventHook<GenderItem>() {
        override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
            return viewHolder.itemView.itemSampleRadioButton
        }

        override fun onClick(
            v: View,
            position: Int,
            fastAdapter: FastAdapter<GenderItem>,
            item: GenderItem
        ) {
            invoke(item.gender)
        }


    }
}
