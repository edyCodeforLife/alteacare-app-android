package id.altea.care.view.specialistsearch.item

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import com.mikepenz.fastadapter.expandable.items.AbstractExpandableItem
import id.altea.care.R
import id.altea.care.view.specialistsearch.model.SpecialistFilterHospitalModel

open class FilterHospitalItem(
    val data: SpecialistFilterHospitalModel,
    val onParentCheck: (Triple<String, String, Boolean>) -> Unit // this triple will be id specialist, name and ischecked
) : AbstractExpandableItem<FilterHospitalItem.ViewHolder>() {

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

    override fun unbindView(holder: ViewHolder) {
        super.unbindView(holder)
        holder.checkBox.text = null
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        var checkBox: MaterialCheckBox = view.findViewById(R.id.itemGeneralCheckbox)

        init {
            checkBox.setOnClickListener {
                data.isSelected = checkBox.isChecked
                onParentCheck(Triple(data.idHospital, data.name, checkBox.isChecked))
            }
        }
    }
}
