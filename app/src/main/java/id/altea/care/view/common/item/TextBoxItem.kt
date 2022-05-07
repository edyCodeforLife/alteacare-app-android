package id.altea.care.view.common.item

import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
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
import id.altea.care.databinding.ItemSingleTextBoxBinding
import id.altea.care.view.address.addressform.item.ItemAddressSingleSelectable
import kotlinx.android.synthetic.main.item_single_simple_cancel.view.*
import kotlinx.coroutines.delay

class TextBoxItem : AbstractBindingItem<ItemSingleTextBoxBinding>() {
    var valueText : String? = ""
    var eventTextListener :  onTextChangeListener?= null

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemSingleTextBoxBinding {
        return ItemSingleTextBoxBinding.inflate(inflater,parent,false)
    }

    override val type: Int
        get() = hashCode()

    override fun bindView(binding: ItemSingleTextBoxBinding, payloads: List<Any>) {
        binding.textContentMessageCallEnded.addTextChangedListener(object  : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s : Editable?) {
                eventTextListener?.textChangeListener(s.toString())
            }

        })


    }

    fun setListenerText(onTextChangeListener: onTextChangeListener){
        this.eventTextListener = onTextChangeListener
    }

    interface onTextChangeListener {
        fun textChangeListener(value  : String)
    }

}