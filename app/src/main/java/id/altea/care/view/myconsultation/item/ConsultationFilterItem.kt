package id.altea.care.view.myconsultation.item

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.mikepenz.fastadapter.listeners.ClickEventHook
import com.mikepenz.fastadapter.select.SelectExtension
import id.altea.care.core.domain.model.PatientFamily
import id.altea.care.core.ext.asBinding
import id.altea.care.databinding.ItemFilterConsultationBinding

class ConsultationFilterItem(var patient: PatientFamily) :
    AbstractBindingItem<ItemFilterConsultationBinding>() {
    override val type: Int
        get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemFilterConsultationBinding {
        return ItemFilterConsultationBinding.inflate(inflater, parent, false)
    }

    @SuppressLint("SetTextI18n")
    override fun bindView(binding: ItemFilterConsultationBinding, payloads: List<Any>) {
        binding.itemFilterRbPatient.isChecked = isSelected
        binding.itemFilterRbPatient.text =
            "${patient.familyRelationType?.name} - ${patient.firstName} ${patient.lastName}"
    }

    fun setSelectedItem(selected: Boolean) {
        isSelected = selected
    }
}

class FilterEventHook(private val onSelected: (PatientFamily?) -> Unit) :
    ClickEventHook<ConsultationFilterItem>() {
    override fun onClick(
        v: View,
        position: Int,
        fastAdapter: FastAdapter<ConsultationFilterItem>,
        item: ConsultationFilterItem
    ) {
        val selectExtension: SelectExtension<ConsultationFilterItem> =
            fastAdapter.requireExtension()
        if (!item.isSelected) {
            val selections = selectExtension.selections
            if (selections.isNotEmpty()) {
                val selectedPosition = selections.iterator().next()
                selectExtension.deselect()
                fastAdapter.notifyItemChanged(selectedPosition)
            }
            selectExtension.select(position)
            onSelected.invoke(item.patient)
        } else {
            selectExtension.deselect(position)
            fastAdapter.notifyItemChanged(position)
            onSelected.invoke(null)
        }
    }

    override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
        return viewHolder.asBinding<ItemFilterConsultationBinding> { it.itemFilterRbPatient }
    }
}
