package id.altea.care.view.specialistsearch.item

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import com.mikepenz.fastadapter.expandable.items.AbstractExpandableItem
import id.altea.care.R
import id.altea.care.view.specialistsearch.model.ChildSpecialistClickModel
import id.altea.care.view.specialistsearch.model.SpecialistFilterSpecializationModel

open class FilterSpecialistChildItem(
    val data: SpecialistFilterSpecializationModel,
    val onChildClickListener: (ChildSpecialistClickModel) -> Unit
) : AbstractExpandableItem<FilterSpecialistChildItem.ViewHolder>() {

    override val layoutRes: Int
        get() = R.layout.item_filter_child_specialist

    override val type: Int
        get() = hashCode()

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    override fun bindView(holder: ViewHolder, payloads: List<Any>) {
        super.bindView(holder, payloads)
        holder.checkBox.text = data.name
        holder.checkBox.isChecked = data.isSelected
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var checkBox: MaterialCheckBox = view.findViewById(R.id.itemGeneralCheckbox)

        init {
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                data.isSelected = isChecked
                val parentData = (parent as FilterSpecialistParentItem).data
                onChildClickListener(
                    ChildSpecialistClickModel(
                        parentData.idSpecialist,
                        parentData.name,
                        data.idSpecialist,
                        data.name,
                        isChecked
                    )
                )
            }
        }
    }

}
