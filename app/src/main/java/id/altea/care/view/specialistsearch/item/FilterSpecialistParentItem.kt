package id.altea.care.view.specialistsearch.item

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import com.mikepenz.fastadapter.ClickListener
import com.mikepenz.fastadapter.IAdapter
import com.mikepenz.fastadapter.IClickable
import com.mikepenz.fastadapter.IExpandable
import com.mikepenz.fastadapter.expandable.items.AbstractExpandableItem
import id.altea.care.R
import id.altea.care.view.specialistsearch.model.SpecialistFilterSpecializationModel

open class FilterSpecialistParentItem(
    val data: SpecialistFilterSpecializationModel,
    val onParentCheck: (Triple<String, String, Boolean>) -> Unit // this triple will be id specialist, name and ischecked
) : AbstractExpandableItem<FilterSpecialistParentItem.ViewHolder>(),
    IClickable<FilterSpecialistParentItem>,
    IExpandable<FilterSpecialistParentItem.ViewHolder> {

    private var mOnClickListener: ClickListener<FilterSpecialistParentItem>? = null

    override val layoutRes: Int
        get() = R.layout.item_filter_parent_specialist

    override val type: Int
        get() = hashCode()

    @Suppress("SetterBackingFieldAssignment")
    override var onItemClickListener: ClickListener<FilterSpecialistParentItem>? =
        { v: View?, adapter: IAdapter<FilterSpecialistParentItem>, item: FilterSpecialistParentItem, position: Int ->
            mOnClickListener?.invoke(v, adapter, item, position) ?: true
        }
        set(onClickListener) {
            this.mOnClickListener = onClickListener // on purpose
        }

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    override fun bindView(holder: ViewHolder, payloads: List<Any>) {
        super.bindView(holder, payloads)
        holder.text.text = data.name
        holder.checkBox.isChecked = data.isSelected
        holder.icon.isVisible = subItems.isNotEmpty()
        if (isExpanded) {
            ViewCompat.animate(holder.icon).rotation(180f).start()
        } else {
            ViewCompat.animate(holder.icon).rotation(0f).start()
        }
    }

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        holder.text.text = null
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var checkBox: MaterialCheckBox = view.findViewById(R.id.itemGeneralCheckbox)
        var text: TextView = view.findViewById(R.id.itemGeneralText)
        var icon: ImageView = view.findViewById(R.id.itemGeneralImgArrow)
        var viewClicked: View = view.findViewById(R.id.itemViewClicked)

        init {
            viewClicked.setOnClickListener {
                if (checkBox.isChecked) {
                    checkBox.isChecked = false
                    data.isSelected = false
                    onParentCheck(Triple(data.idSpecialist, data.name, false))
                } else {
                    checkBox.isChecked = true
                    data.isSelected = true
                    onParentCheck(Triple(data.idSpecialist, data.name, true))
                }
            }
        }
    }

    override var onPreItemClickListener: ClickListener<FilterSpecialistParentItem>?
        get() = null
        set(_) {}

}
