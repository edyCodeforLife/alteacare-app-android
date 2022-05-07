package id.altea.care.view.faq.item

import android.view.View
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.ISubItem
import com.mikepenz.fastadapter.expandable.items.AbstractExpandableItem
import id.altea.care.R
import kotlinx.android.synthetic.main.item_faq.view.*

class FaqItem(private val content: String) : AbstractExpandableItem<FaqItem.ViewHolderFaqItem>(),
    ISubItem<FaqItem.ViewHolderFaqItem> {

    override val type: Int
        get() = hashCode()

    override val layoutRes: Int
        get() = R.layout.item_faq

    override fun getViewHolder(v: View): ViewHolderFaqItem = ViewHolderFaqItem(v)

    inner class ViewHolderFaqItem(val view: View) : FastAdapter.ViewHolder<FaqItem>(view) {
        override fun bindView(item: FaqItem, payloads: List<Any>) {
            view.itemTextContent.loadData(item.content,"text/html", "utf-8")
        }

        override fun unbindView(item: FaqItem) {
            view.itemTextContent.loadData("","text/html", "utf-8")
        }
    }
}