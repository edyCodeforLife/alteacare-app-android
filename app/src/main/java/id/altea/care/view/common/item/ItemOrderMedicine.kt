package id.altea.care.view.common.item

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import id.altea.care.core.domain.model.OrderMedicine
import id.altea.care.databinding.ItemOrderMedicineBinding


class ItemOrderMedicine (private val orderMedicine : OrderMedicine) :
    AbstractBindingItem<ItemOrderMedicineBinding>() {

    override val type: Int get() = hashCode()

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): ItemOrderMedicineBinding {
        return ItemOrderMedicineBinding.inflate(inflater, parent, false)
    }

    override fun bindView(binding: ItemOrderMedicineBinding, payloads: List<Any>) {
        binding.run {
            circleImageView.setImageResource(orderMedicine.image)
            itemTextTitleOrderMedicine.text = orderMedicine.title
            itemTextDescriptionOrderMedicine.text = orderMedicine.description
        }

    }

}