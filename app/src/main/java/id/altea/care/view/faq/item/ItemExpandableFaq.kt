package id.altea.care.view.faq.item

import android.view.View
import androidx.core.view.ViewCompat
import com.mikepenz.fastadapter.ClickListener
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.IClickable
import com.mikepenz.fastadapter.expandable.items.AbstractExpandableItem
import id.altea.care.R
import kotlinx.android.synthetic.main.item_expandable_faq.view.*

class ItemExpandableFaq(val text : String) : AbstractExpandableItem<ItemExpandableFaq.ViewHolderExpandableFaq>(),
    IClickable<ItemExpandableFaq> {

    private var clickListener: ClickListener<ItemExpandableFaq>? = null

    override val layoutRes: Int
        get() = R.layout.item_expandable_faq


    override val type: Int
        get() = hashCode()

    override fun getViewHolder(v: View): ViewHolderExpandableFaq  = ViewHolderExpandableFaq(v)

    inner class ViewHolderExpandableFaq(val view : View) : FastAdapter.ViewHolder<ItemExpandableFaq>(view){
        override fun bindView(item: ItemExpandableFaq, payloads: List<Any>) {
            view.run {
                clearAnimation()
                itemExpandapleTitle.text = item.text
                if (item.subItems.isEmpty()) {
                    itemExpandleIcon.visibility = View.GONE
                } else {
                    itemExpandleIcon.visibility = View.VISIBLE
                }

                if (item.isExpanded) {
                    itemExpandleIcon.rotation = 180f
                } else {
                    itemExpandleIcon.rotation = 0f
                }
            }
        }

        override fun unbindView(item: ItemExpandableFaq) {
            view.itemExpandapleTitle.text = null
            view.itemExpandleIcon.clearAnimation()
        }

    }

    override var onItemClickListener: ClickListener<ItemExpandableFaq>? =  { v: View?, adapter: IAdapter<ItemExpandableFaq>, item: ItemExpandableFaq, position: Int ->
        if (item.subItems.isNotEmpty()) {
            v?.itemExpandleIcon?.let {
                if (!item.isExpanded) {
                    ViewCompat.animate(it).rotation(0f).start()
                } else {
                    ViewCompat.animate(it).rotation(180f).start()
                }
            }
        }
        clickListener?.invoke(v, adapter, item, position) ?: true
    }

    set(value) {
        clickListener = value
        field = value
    }

    override var isSelectable: Boolean
        get() = subItems.isEmpty()
        set(value) {
            super.isSelectable = value
        }


    override var onPreItemClickListener: ClickListener<ItemExpandableFaq>?
        get() = null
        set(value) {}
}